# 🌌 Constellation Games

An interactive, creative constellation application exploring celestial visual art through Java Swing desktop graphics and an interactive 3D particle canvas using Three.js and MediaPipe.

---

## 🌟 Project Overview

**Constellation Games** combines art and technology to allow users to interact with stars and constellations across two distinct interactive experiences:

1. **Java Swing Constellation Drawer** (`constellation_java`): A desktop GUI application built in Java. Users can interactively place stars on dynamic celestial backdrops, change connecting line styles (solid, dashed, gradient, glowing halo), and clear canvas states.
2. **MediaPipe 3D Particle Constellation** (`constellation_mediaPipe`): An interactive 3D web application built with Three.js. Features 7,000 glowing particles that react dynamically to cursor and pointer movement as an interactive celestial force field, with experimental MediaPipe webcam hand tracking integration.

---

## 📸 Visual Previews & Screenshots

| Java Swing Constellation Drawer | MediaPipe 3D Interactive Canvas |
| :---: | :---: |
| ![Java Swing Constellation Drawer](docs/screenshots/java_drawer.png) | ![MediaPipe 3D Interactive Canvas](docs/screenshots/mediapipe_3d.png) |

---

## 🛠️ Technology Stack & Prerequisites

### ☕ Module 1: `constellation_java` (Desktop App)
* **Language:** Java 17+
* **GUI Toolkit:** Java Swing / AWT
* **Prerequisites:** JDK 17+ installed on your system.

### ⚡ Module 2: `constellation_mediaPipe` (Web App)
* **Language/Framework:** JavaScript (ES Modules), HTML5, CSS3, Vite
* **3D & Vision Libraries:** Three.js, MediaPipe Tasks Vision (`@mediapipe/tasks-vision`)
* **Post-Processing:** `EffectComposer`, `UnrealBloomPass`, `AfterimagePass`, `VignetteShader`
* **Prerequisites:** Node.js v18+ and `npm` installed (webcam optional for experimental hand tracking).

---

## 📁 Repository Architecture & Navigation

```
Constellation_Games/
├── .github/
│   └── workflows/
│       └── ci.yml               # GitHub Actions CI workflow for Java & Node
├── constellation_java/          # Java Swing Desktop Application
│   └── src/
│       └── constellation/
│           ├── ConstellationDrawer.java  # Main application entry point & UI frame
│           ├── DrawingPanel.java         # Interactive drawing canvas & background manager
│           ├── Star.java                 # Star entity class with custom visual styles
│           └── images/                   # Space & nebula background assets
├── constellation_mediaPipe/     # Web 3D Interactive Particle Canvas
│   ├── index.html               # Main HTML entry document
│   ├── main.js                  # Three.js scene setup & MediaPipe landmarker module
│   └── package.json             # NPM dependencies & scripts (Vite, Three.js)
├── .gitignore                   # Root Git ignore rules (Java & Node)
├── LICENSE                      # MIT Open-source License
└── README.md                    # Project documentation
```

---

## 🚀 Setup & Local Execution Instructions

### Running the Java Desktop Application (`constellation_java`)

1. Open your terminal and navigate to the Java project directory:
   ```bash
   cd constellation_java
   ```
2. Compile the Java source files:
   ```bash
   javac -encoding UTF-8 -d bin src/constellation/*.java
   ```
3. Run the application:
   ```bash
   java -cp bin constellation.ConstellationDrawer
   ```
4. **Controls:**
   * **Mouse Left Click:** Place a star on the canvas.
   * **Random Background:** Switch between high-resolution nebula images or cosmic color gradients.
   * **Line Style:** Cycle connecting line styles (*Solid*, *Dashed*, *Gradient*, *Glowing*).
   * **Clear Stars:** Reset canvas.

---

### Running the 3D Interactive Canvas (`constellation_mediaPipe`)

1. Navigate to the MediaPipe project directory:
   ```bash
   cd constellation_mediaPipe
   ```
2. Install npm dependencies:
   ```bash
   npm install
   ```
3. Start the Vite local development server:
   ```bash
   npm run dev
   ```
4. Open the browser URL displayed in the terminal (typically `http://localhost:5173`).
5. **Usage & Interaction:**
   * Move your cursor or pointer across the screen to interactively repel and illuminate 3D particles in real time.
   * (Optional) Allow webcam permissions to test experimental MediaPipe hand tracking integration.

---

## 🤝 Governance & License

This project is open-source under the **[MIT License](LICENSE)**. Contributions, bug reports, and enhancements are welcome!
