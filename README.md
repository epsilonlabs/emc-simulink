# Epsilon-Simulink Integration

Eclipse plugins that extend Epsilon's Model Connectivity (EMC) layer with support for managing Simulink models using languages of the Epsilon platform, to pefrorm activities such as code generation, model validation and model-to-model transformation.

# Installation

* Install the latest **interim** version of Epsilon
* Clone the repository
* Import all projects in your Eclipse workspace
* Run a nested Eclipse instance with the VM argument 
  * `-Djava.library.path=<matlab-root>/bin/maci64 (change `maci46` to the `win64` if on Windows)

# Quick start
* Create a new general project (in your nested Eclipse instance)
* Create an empty Simulink model in the project (e.g. empty.slx)
* Create a new EOL file (e.g. demo.eol) and add the content below to it:

```javascript
// Create elements
var sineWave = new `simulink/Sources/Sine Wave`;
var gain = new `simulink/Math Operations/Gain`;
var saturation = new `simulink/Discontinuities/Saturation`;
var busCreator = new `simulink/Signal Routing/Bus Creator`;
var scope = new `simulink/Sinks/Scope`;

// Position them on the diagram
sineWave.position = "[100 100 130 130]";
gain.position = "[200 100 230 130]";
saturation.position = "[300 100 330 130]";
busCreator.position = "[400 70 410 300]";
scope.position = "[500 175 530 205]";

// Set their properties
gain.gain = 2;
busCreator.inputs = 3;

// Link them
sineWave.link(gain);
gain.link(saturation);
saturation.link(busCreator);
gain.linkTo(busCreator, 2);
sineWave.linkTo(busCreator, 3);
busCreator.link(scope);
```

* Create a new Run configuration for your EOL program and add a Simulink model to it, pointing at your .slx (e.g. empty.slx) model
* Launch the Run configuration. Simulink should pop up and you should see the model below (which you can edit/run as normal)

![Generated Simulink model](https://raw.githubusercontent.com/wiki/epsilonlabs/emc-simulink/simulink-model.png)
