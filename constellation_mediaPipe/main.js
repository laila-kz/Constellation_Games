import * as THREE from 'three'
import { HandLandmarker, FilesetResolver } from '@mediapipe/tasks-vision'
import { EffectComposer } from 'three/examples/jsm/postprocessing/EffectComposer.js'
import { RenderPass } from 'three/examples/jsm/postprocessing/RenderPass.js'
import { UnrealBloomPass } from 'three/examples/jsm/postprocessing/UnrealBloomPass.js'
import { AfterimagePass } from 'three/examples/jsm/postprocessing/AfterimagePass.js'
import { ShaderPass } from 'three/examples/jsm/postprocessing/ShaderPass.js'
import { VignetteShader } from 'three/examples/jsm/shaders/VignetteShader.js'

// --------------------
// THREE.JS SETUP
// --------------------

const scene = new THREE.Scene()
scene.background = new THREE.Color(0x0b1020)
scene.fog = new THREE.Fog(0x0b1020, 2.0, 10.5)

const camera = new THREE.PerspectiveCamera(
  75,
  window.innerWidth / window.innerHeight,
  0.1,
  1000
)
camera.position.z = 5

const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: false })
renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
renderer.setClearColor(0x0b1020, 1)
renderer.setSize(window.innerWidth, window.innerHeight)
document.body.appendChild(renderer.domElement)

let composer
let bloomPass

window.addEventListener('resize', () => {
  camera.aspect = window.innerWidth / window.innerHeight
  camera.updateProjectionMatrix()
  renderer.setSize(window.innerWidth, window.innerHeight)
  if (composer) {
    composer.setSize(window.innerWidth, window.innerHeight)
  }
  if (bloomPass) {
    bloomPass.setSize(window.innerWidth, window.innerHeight)
  }
})

// --------------------
// PARTICLES
// --------------------

const particleCount = 7000
const starRatio = 0.18

const positions = new Float32Array(particleCount * 3)
const originalPositions = new Float32Array(particleCount * 3)
const velocities = new Float32Array(particleCount * 3)
const starIndices = []
const roundIndices = []

for (let i = 0; i < particleCount; i++) {
  const x = (Math.random() - 0.5) * 5
  const y = (Math.random() - 0.5) * 5
  const z = (Math.random() - 0.5) * 5

  const i3 = i * 3

  positions[i3] = x
  positions[i3 + 1] = y
  positions[i3 + 2] = z

  originalPositions[i3] = x
  originalPositions[i3 + 1] = y
  originalPositions[i3 + 2] = z

  if (Math.random() < starRatio) {
    starIndices.push(i)
  } else {
    roundIndices.push(i)
  }
}

const roundPositions = new Float32Array(roundIndices.length * 3)
const starPositions = new Float32Array(starIndices.length * 3)
const roundColors = new Float32Array(roundIndices.length * 3)
const starColors = new Float32Array(starIndices.length * 3)

const roundGeometry = new THREE.BufferGeometry()
roundGeometry.setAttribute('position', new THREE.BufferAttribute(roundPositions, 3))
roundGeometry.setAttribute('color', new THREE.BufferAttribute(roundColors, 3))

const starGeometry = new THREE.BufferGeometry()
starGeometry.setAttribute('position', new THREE.BufferAttribute(starPositions, 3))
starGeometry.setAttribute('color', new THREE.BufferAttribute(starColors, 3))

const roundMaterial = new THREE.PointsMaterial({
  size: 0.024,
  blending: THREE.AdditiveBlending,
  transparent: true,
  depthWrite: false,
  vertexColors: true
})

const glowMaterial = new THREE.PointsMaterial({
  size: 0.06,
  blending: THREE.AdditiveBlending,
  transparent: true,
  opacity: 0.2,
  depthWrite: false,
  vertexColors: true
})

const starMaterial = new THREE.PointsMaterial({
  size: 0.03,
  blending: THREE.AdditiveBlending,
  transparent: true,
  depthWrite: false,
  vertexColors: true,
  map: createStarTexture(),
  alphaTest: 0.2
})

const roundPoints = new THREE.Points(roundGeometry, roundMaterial)
const glowPoints = new THREE.Points(roundGeometry, glowMaterial)
const starPoints = new THREE.Points(starGeometry, starMaterial)

scene.add(glowPoints)
scene.add(roundPoints)
scene.add(starPoints)

function createStarTexture() {
  const size = 96
  const canvas = document.createElement('canvas')
  canvas.width = size
  canvas.height = size
  const ctx = canvas.getContext('2d')
  if (!ctx) {
    return new THREE.CanvasTexture(canvas)
  }

  const cx = size / 2
  const cy = size / 2
  const spikes = 5
  const outerRadius = size * 0.42
  const innerRadius = size * 0.18

  ctx.clearRect(0, 0, size, size)
  ctx.beginPath()
  for (let i = 0; i < spikes * 2; i++) {
    const angle = (Math.PI / spikes) * i - Math.PI / 2
    const radius = i % 2 === 0 ? outerRadius : innerRadius
    ctx.lineTo(cx + Math.cos(angle) * radius, cy + Math.sin(angle) * radius)
  }
  ctx.closePath()

  const gradient = ctx.createRadialGradient(cx, cy, innerRadius * 0.2, cx, cy, outerRadius)
  gradient.addColorStop(0, 'rgba(255, 255, 255, 0.95)')
  gradient.addColorStop(0.6, 'rgba(255, 255, 255, 0.55)')
  gradient.addColorStop(1, 'rgba(255, 255, 255, 0.0)')
  ctx.fillStyle = gradient
  ctx.fill()

  const texture = new THREE.CanvasTexture(canvas)
  texture.minFilter = THREE.LinearFilter
  texture.magFilter = THREE.LinearFilter
  texture.needsUpdate = true
  return texture
}

// --------------------
// HAND TRACKING & POINTER INTERACTION
// --------------------

let handLandmarker = null
let runningMode = "VIDEO"
let lastHandDetectedTime = 0

const video = document.getElementById('video')
const statusText = document.getElementById('status-text')
const statusDot = document.getElementById('status-dot')

function updateStatus(message, color) {
  if (statusText) statusText.textContent = message
  if (statusDot) statusDot.style.backgroundColor = color
}

let handPosition = new THREE.Vector3()
let targetHandPosition = new THREE.Vector3()
let lastDetectionTime = 0

// Pointer / Mouse Fallback (only takes over if hand is not detected for >1.5 seconds)
window.addEventListener('pointermove', (e) => {
  if (performance.now() - lastHandDetectedTime > 1500) {
    targetHandPosition.x = (e.clientX / window.innerWidth - 0.5) * 6
    targetHandPosition.y = -(e.clientY / window.innerHeight - 0.5) * 6
    targetHandPosition.z = 0
  }
})

async function createHandLandmarker() {
  try {
    updateStatus("Loading MediaPipe Hand Model...", "#f59e0b")
    const vision = await FilesetResolver.forVisionTasks(
      "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@0.10.32/wasm"
    )

    handLandmarker = await HandLandmarker.createFromOptions(vision, {
      baseOptions: {
        modelAssetPath:
          "https://storage.googleapis.com/mediapipe-models/hand_landmarker/hand_landmarker/float16/1/hand_landmarker.task"
      },
      runningMode,
      numHands: 1
    })
    updateStatus("Webcam Hand Tracking Active (Show hand to camera)", "#10b981")
  } catch (err) {
    console.warn("MediaPipe model load failed, active mouse mode:", err)
    updateStatus("Mouse Pointer Active (Hand tracking model unavailable)", "#3b82f6")
  }
}

createHandLandmarker()

if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
  navigator.mediaDevices.getUserMedia({ video: { width: 640, height: 480 } })
    .then(stream => {
      if (video) {
        video.srcObject = stream
        video.onloadedmetadata = () => {
          video.play()
          requestAnimationFrame(detectHands)
        }
      }
    })
    .catch(err => {
      console.warn("Webcam access not granted. Mouse interaction active:", err)
      updateStatus("Mouse Pointer Active (Webcam permission required for hand tracking)", "#3b82f6")
    })
}

async function detectHands() {
  if (video && video.currentTime > 0 && !video.paused && !video.ended && handLandmarker) {
    const now = performance.now()
    if (now - lastDetectionTime > 30) {
      lastDetectionTime = now
      try {
        const results = handLandmarker.detectForVideo(video, now)
        if (results && results.landmarks && results.landmarks.length > 0) {
          lastHandDetectedTime = now
          const indexTip = results.landmarks[0][8]

          // Mirror horizontal X coordinate for natural front-facing webcam movement
          targetHandPosition.x = (0.5 - indexTip.x) * 6
          targetHandPosition.y = -(indexTip.y - 0.5) * 6
          targetHandPosition.z = -indexTip.z * 6

          updateStatus("✋ Hand Tracked (Repelling Particles)", "#10b981")
        } else {
          if (now - lastHandDetectedTime > 1500) {
            updateStatus("🖱️ Mouse Pointer Active (Show hand to camera)", "#3b82f6")
          }
        }
      } catch (err) {
        console.warn("Detection frame error:", err)
      }
    }
  }

  requestAnimationFrame(detectHands)
}

// --------------------
// ANIMATION LOOP & POSTPROCESSING
// --------------------

const radius = 1.6
const radiusSq = radius * radius
const colorHelper = new THREE.Color()
const depthRange = 3.0

composer = new EffectComposer(renderer)
const renderPass = new RenderPass(scene, camera)
const afterimagePass = new AfterimagePass()
afterimagePass.uniforms.damp.value = 0.88

bloomPass = new UnrealBloomPass(
  new THREE.Vector2(window.innerWidth, window.innerHeight),
  0.9,
  0.6,
  0.15
)

const vignettePass = new ShaderPass(VignetteShader)
vignettePass.uniforms.offset.value = 1.05
vignettePass.uniforms.darkness.value = 1.25

composer.addPass(renderPass)
composer.addPass(afterimagePass)
composer.addPass(bloomPass)
composer.addPass(vignettePass)

function updateSubsetPositionsAndColors(indices, targetPositions, targetColors) {
  for (let i = 0; i < indices.length; i++) {
    const index = indices[i]
    const i3 = index * 3
    const t3 = i * 3

    const px = positions[i3]
    const py = positions[i3 + 1]
    const pz = positions[i3 + 2]

    targetPositions[t3] = px
    targetPositions[t3 + 1] = py
    targetPositions[t3 + 2] = pz

    const dx = px - handPosition.x
    const dy = py - handPosition.y
    const dz = pz - handPosition.z
    const distSq = dx * dx + dy * dy + dz * dz
    const distNorm = Math.min(distSq / radiusSq, 1)
    const depthNorm = THREE.MathUtils.clamp((pz + depthRange) / (depthRange * 2), 0, 1)

    const hue = 0.56 + 0.12 * (1 - depthNorm) + 0.1 * (1 - distNorm)
    const saturation = 0.38
    const lightness = 0.72 + 0.08 * (1 - distNorm)

    colorHelper.setHSL(hue % 1, saturation, Math.min(lightness, 0.85))

    targetColors[t3] = colorHelper.r
    targetColors[t3 + 1] = colorHelper.g
    targetColors[t3 + 2] = colorHelper.b
  }
}

function animate() {
  requestAnimationFrame(animate)

  handPosition.lerp(targetHandPosition, 0.25)

  const pos = positions
  const vel = velocities
  const orig = originalPositions

  for (let i = 0; i < particleCount; i++) {
    const i3 = i * 3

    const dx = pos[i3] - handPosition.x
    const dy = pos[i3 + 1] - handPosition.y
    const dz = pos[i3 + 2] - handPosition.z

    const distSq = dx * dx + dy * dy + dz * dz

    // Repulsion
    if (distSq < radiusSq) {
      const force = (radiusSq - distSq) / radiusSq

      vel[i3]     += dx * force * 0.2
      vel[i3 + 1] += dy * force * 0.2
      vel[i3 + 2] += dz * force * 0.2
    }

    // Spring back
    vel[i3]     += (orig[i3]     - pos[i3])     * 0.002
    vel[i3 + 1] += (orig[i3 + 1] - pos[i3 + 1]) * 0.002
    vel[i3 + 2] += (orig[i3 + 2] - pos[i3 + 2]) * 0.002

    // Damping
    vel[i3]     *= 0.92
    vel[i3 + 1] *= 0.92
    vel[i3 + 2] *= 0.92

    pos[i3]     += vel[i3]
    pos[i3 + 1] += vel[i3 + 1]
    pos[i3 + 2] += vel[i3 + 2]
  }

  updateSubsetPositionsAndColors(roundIndices, roundPositions, roundColors)
  updateSubsetPositionsAndColors(starIndices, starPositions, starColors)

  roundGeometry.attributes.position.needsUpdate = true
  starGeometry.attributes.position.needsUpdate = true
  roundGeometry.attributes.color.needsUpdate = true
  starGeometry.attributes.color.needsUpdate = true

  composer.render()
}

animate()