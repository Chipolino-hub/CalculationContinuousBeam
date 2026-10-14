# CalculationContinuousBeam

A Java / JavaFX educational desktop application for calculating **continuous beams** using the **three-moment method**.

## Features

- Define multiple spans, lengths and moments of inertia.
- Add distributed loads, point loads and applied moments.
- Calculate support bending moments and support reactions.
- Display a bending moment diagram and calculation report.

## Requirements

- JDK 22 (the supplied Eclipse project is configured for Java 22).
- JavaFX 21 SDK.
- Eclipse IDE, or another Java IDE configured for JavaFX.

## Run in Eclipse

1. Import this directory with **File → Import → Existing Projects into Workspace**.
2. Configure a Java 22 JDK.
3. Create an Eclipse User Library called `JavaFX21` and add the JavaFX SDK JARs from `lib` (the project's `.classpath` expects this name).
4. If needed, specify VM arguments: `--module-path "PATH_TO_JAVAFX_SDK/lib" --add-modules javafx.controls,javafx.fxml`.
5. Run `application.BeamCalculator`.

## Project structure

- `src/application/` — Java source code, FXML and CSS.
- `src/module-info.java` — Java module descriptor.
- `.project`, `.classpath`, `.settings/` — Eclipse project configuration.

## Notes

Educational engineering calculator; validate results independently before using for safety-critical structural design. GUI launch has not been verified in this repository preparation.
