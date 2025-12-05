# DatCon (aero-oli fork)

DatCon is a Java desktop application for decoding DJI flight controller `.DAT` log files into more useful formats such as CSV and KML. These outputs can then be loaded into tools like Excel, Google Earth, or dedicated analysis software for post-flight analysis and forensics.

This repository is a fork of the original open-source DatCon 3.5.0 codebase, with some light refactoring and new quality-of-life features, including an experimental batch-processing "job queue" and a refreshed Swing GUI.

> **Note:** This is a developer-oriented fork. If you just want prebuilt binaries, use the official DatCon downloads from [datfile.net](https://datfile.net/).

---

## Table of contents

- [Overview](#overview)
- [What this fork adds](#what-this-fork-adds)
- [Capabilities](#capabilities)
  - [Supported inputs](#supported-inputs)
  - [Output formats](#output-formats)
  - [Time axis & signals](#time-axis--signals)
- [Project layout](#project-layout)
- [Getting started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Cloning the repository](#cloning-the-repository)
- [Building & running](#building--running)
  - [Option 1: Using Eclipse (recommended)](#option-1-using-eclipse-recommended)
  - [Option 2: Command line build](#option-2-command-line-build)
- [Using DatCon](#using-datcon)
  - [Quick start (single file)](#quick-start-single-file)
  - [Batch / queue workflow in this fork](#batch--queue-workflow-in-this-fork)
- [Limitations & known issues](#limitations--known-issues)
- [License](#license)
- [Credits & acknowledgements](#credits--acknowledgements)

---

## Overview

DatCon is a free, offline Java tool for working with DJI flight controller `.DAT` logs. It:

- Reads raw `.DAT` files produced by various DJI aircraft and by the DJI Go / Fly tablet apps.
- Decodes the internal binary record structures.
- Outputs:
  - Time-series CSV containing a large number of telemetry "signals".
  - Google Earth-compatible KML tracks.
  - Text files for event logs, configuration logs, and record definitions.

The produced CSV and KML files are intended for further analysis in tools such as:

- Excel / LibreOffice Calc / other spreadsheet tools
- CsvView (a companion viewer)
- Google Earth (for spatial visualisation)
- Custom analysis pipelines or scripts

This fork keeps all of that core behaviour and layers on a few usability improvements.

---

## What this fork adds

Compared to the original `BudWalkerJava/DatCon` repository, this fork primarily focuses on:

- **Batch processing / job queue**
  - New `DatJob` class: represents a single `.DAT` file plus its per-file settings (time interval, offset, status, error message, etc.).
  - New `FileQueuePanel` GUI panel:
    - Shows a list of `.DAT` files to be processed as a batch.
    - Lets you **add**, **remove**, and **clear** queued items.
    - Selecting a file makes it the "active" one for configuring time/offset and output options.
- **GUI polish**
  - Refactored Swing panels (`CsvPanel`, `FileQueuePanel`, `KMLPanel`, `LogFilesPanel`, `OffsetPanel`, `TimeAxisPanel`, and `DatCon`) for:
    - More consistent fonts and borders.
    - Slightly more modern panel styling and spacing.
    - Clearer grouping of related controls.

The intention is to keep DatCon's behaviour familiar while making it easier to use for:

- Processing multiple `.DAT` logs in one go.
- Working with consistent time/offset settings across a batch.

All core decoding logic is still the original DatCon implementation.

---

## Capabilities

### Supported inputs

DatCon can read a range of **un-encrypted** DJI `.DAT` logs, notably:

- Aircraft `.DAT` from:
  - Phantom 3 series
  - Phantom 4 / Phantom 4 Pro
  - Inspire 1 / Inspire 2
  - Some Matrice models (e.g. M100, M600)
  - Mavic Pro
- `.DAT` files produced by the DJI Go / Fly tablet apps (including "V3" exported logs).

Logs from aircraft that write **encrypted** `.DAT` files (for example some Mavic Air / Mavic 2 / newer models) cannot be decoded by DatCon and will fail to parse.

If you need help retrieving `.DAT` logs from the aircraft or tablet, the original DatCon documentation on [datfile.net](https://datfile.net/) includes step-by-step guides.

### Output formats

From a single `.DAT` you can generate:

- **CSV**
  - Time-series telemetry with one column per signal.
  - Adjustable sample rate (e.g. 1 Hz – 200 Hz) to trade off precision vs file size.
  - Optional inclusion of event log entries as a column.
- **KML**
  - Ground track and 3D flight profile for Google Earth.
  - Optional profiling that uses home-point elevation for a proper 3D track.
- **Log text files**
  - **EventLog** – discrete events (mode switches, RTH, etc.) annotated with timestamps.
  - **ConfigLog** – configuration / initialisation parameters and build information.
  - **RecDefs** – record definitions used to decode DatDefined signals.

All outputs by default are written next to the source `.DAT` file, with sensible filenames that include the `.DAT` base name.

### Time axis & signals

DatCon offers a fairly sophisticated view of time and signals:

- **Time axis / interval**
  - "Smart" time-axis behaviour that can align `t = 0` to:
    - Flight start (if a flight is present).
    - Motor start.
    - Recording start.
  - Ability to trim to a specific time range to focus on an event or reduce file size.
- **Signal categories**
  - **DatDefined** signals (`:D` suffix) – directly from `.DAT` record fields.
  - **Engineered** signals – renamed / rescaled / unit-annotated fields for easier plotting.
  - **Computed** signals (`:C` suffix) – derived values (e.g. headings from quaternions, etc.).
  - You can choose whether to output:
    - All signal groups.
    - Engineered + computed only.
    - Raw DatDefined signals only.

---

## Project layout

At a high level:

```
.
├── DatCon/          # Java project (Eclipse-style)
│   ├── .project     # Eclipse project metadata
│   └── src/
│       ├── apps/    # Application entry points (main GUI: src.apps.DatCon)
│       ├── GUI/     # Swing UI panels (CsvPanel, FileQueuePanel, KMLPanel, etc.)
│       ├── Files/   # File-related logic (ConvertDat, CsvWriter, DatJob, DatFile, ...)
│       └── ...      # Record definitions and parsing logic
├── LICENSE.md
└── README.md        # (this file)
```

The main class for the GUI application is:

```
src.apps.DatCon
```

---

## Getting started

### Prerequisites

You'll need:

- **Java Development Kit (JDK)** – 64-bit, version 8 or newer is recommended
- **Git** – to clone this repository
- A Java-capable IDE (Eclipse, IntelliJ, VS Code) or command-line environment

### Cloning the repository

```bash
git clone https://github.com/aero-oli/DatCon.git
cd DatCon
```

All Java sources live under the `DatCon/` subdirectory.

---

## Building & running

### Option 1: Using Eclipse (recommended)

This fork includes an `.project` file, so it imports cleanly into Eclipse.

1. **Start Eclipse.**
2. Select **File → Import… → Existing Projects into Workspace**.
3. Choose the cloned `DatCon/DatCon` folder as the root directory.
4. Ensure the project uses a **Java 8+ JRE/JDK** (Project → Properties → Java Build Path).
5. Build the project (Eclipse usually builds automatically).
6. In the **Package Explorer**, locate `src.apps.DatCon`.
7. Right-click `DatCon.java` → **Run As → Java Application**.

The DatCon GUI should open. From there you can load `.DAT` files and convert them.

### Option 2: Command line build

If you prefer not to use an IDE, you can compile and package a runnable JAR manually.

From the repository root:

```bash
cd DatCon

# Create an output directory
mkdir -p build/classes

# Compile all Java sources
find src -name "*.java" > sources.txt
javac -d build/classes @sources.txt

# Package into a runnable JAR
jar cfe DatCon.jar src.apps.DatCon -C build/classes .
```

To run:

```bash
java -jar DatCon.jar
```

> On Windows, you can adapt the commands for PowerShell or use Git Bash; the important parts are:
>
> - `javac` compiling everything under `src` into `build/classes`
> - `jar cfe` setting the main class to `src.apps.DatCon`

There are no external dependencies beyond the standard Java runtime.

---

## Using DatCon

### Quick start (single file)

Once the application is running:

1. **Select the `.DAT` file**
   - Click in the `.DAT` file field at the top of the window.
   - Choose the DJI `.DAT` log you want to analyze.

2. **(Optional) Adjust the time axis**
   - Use the **Time Axis** panel to:
     - Choose the time range to export (start/end).
     - Decide how `t = 0` should be aligned (e.g. flight start).

3. **Choose outputs**
   - In the **CSV** panel:
     - Pick a sample rate.
     - Toggle whether to include the event log column.
   - In the **KML** panel:
     - Enable ground track and/or 3D profile.
     - Set home-point elevation if needed.
   - In the **Log Files** panel:
     - Select which of EventLog / ConfigLog / RecDefs to generate.

4. **Click `GO!`**
   - DatCon will process the `.DAT`.
   - Output files (CSV, KML, logs) will be written to the same directory as the `.DAT`.

5. **Open results**
   - Use your preferred tools (Excel, Google Earth, etc.) to view the output.

### Batch / queue workflow in this fork

The main addition in this fork is a basic batch-processing workflow.

You'll see a **File Queue** panel that:

- Shows a list of `.DAT` files to be processed as a batch.
- Provides buttons:
  - **Add .DAT files** – select one or more logs to add.
  - **Remove Selected** – remove the currently highlighted job.
  - **Clear** – empty the queue.

Workflow:

1. **Add multiple `.DAT` logs**
   - Click **Add .DAT files** and select any number of `.DAT` files.
   - Each file becomes a `DatJob` with an initial status of `PENDING`.

2. **Configure per-file settings**
   - Click on a file in the queue to select it.
   - Use the **Time Axis**, **CSV**, **KML**, and **Log Files** panels to set:
     - Time interval (`tickLower` / `tickUpper`).
     - Time offset.
     - Which outputs to generate.
   - Those settings are stored on the corresponding `DatJob`.

3. **Run the batch**
   - When you start processing (e.g. via `GO!`), DatCon walks over the queue and:
     - Analyzes each `.DAT` file.
     - Applies the stored time/offset settings.
     - Generates the selected outputs.
     - Updates job status through states like `ANALYZING`, `READY`, `PROCESSING`, `DONE`, or `ERROR`.

This is particularly useful if you regularly process many flights using the same configuration.

---

## Limitations & known issues

### Encrypted logs

Some newer DJI aircraft produce encrypted `.DAT` files (especially newer Mavic series and some newer models). These cannot be decoded by DatCon and will fail to process.

### Model coverage

Support is focused on aircraft and log formats contemporary with DatCon 3.x. Newer platforms may not be fully supported even if their `.DAT` files are not encrypted.

### Version mismatch with official binaries

The official DatCon downloads on datfile.net may be newer (e.g. DatCon 4.x) than this open-source codebase. Features present in 4.x but implemented after 3.5.0 may not exist here.

### No official releases in this fork

This repository does not publish GitHub releases or prebuilt jars; it's intended for developers comfortable building from source.

### Troubleshooting

If you hit parsing problems on a specific `.DAT`:

- Confirm the log is not encrypted
- Capture the error message and context
- Open a GitHub issue on this fork or refer to the upstream documentation/contacts

---

## License

The code is distributed under a permissive license with a warranty disclaimer.

In summary (not legal advice):

- Redistribution and use in source and binary forms, with or without modification, are permitted
- Redistributions must include the disclaimer from `LICENSE.md`
- The software is provided **"AS IS"**, without any express or implied warranties; the creator and contributors are not liable for damages

See [`LICENSE.md`](LICENSE.md) for the full text.

---

## Credits & acknowledgements

- **Original DatCon** created and maintained by Rowland / BudWalker and contributors
- **Official documentation** available at [datfile.net](https://datfile.net/)
- **This fork** (`aero-oli/DatCon`) adds:
  - Batch-processing job abstractions (`DatJob`)
  - A `FileQueuePanel` for managing multiple `.DAT` files
  - GUI styling and layout tweaks across several core panels

This fork is not affiliated with DJI or the original DatCon author; it is provided purely for research, analysis, and educational purposes.

---

> The capabilities and behavioural descriptions above are aligned with the original DatCon docs and usage notes from datfile.net and its DatCon 4 user guide PDF.
