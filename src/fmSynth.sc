s.boot;
s.scope;
FreqScope.new;

///////////////
// functions //
///////////////

(
~levelTable = {
	var i = 128;
	var n = 10;
	Array.fill2D(n,n,{
		case
		{ i == 128 } { i=i-1; i }
		{ i<128 && i>122 } { i=i-5; i }
		{ i<=122 && i>110 } { i=i-4; i }
		{ i<=110 && i>104 } { i=i-3; i }
		{ i<=104 && i>86 } { i=i-2; i }
		{ i<=86 && i>84 } { i=i-1; i }
		{ i<=84 && i>82 } { i=i-2; i }
		{ i<=82 && i>81 } { i=i-1; i }
		{ i<=81 && i>79 } { i=i-2; i }
		{ i<=79 && i>0 } { i=i-1; i };
	})
}.value;

~outLevelToTL = {
	arg outputLevel;
	var n = 10;
	var row = outputLevel/n;
	var col = outputLevel%n;
	~levelTable.at(row.asInteger).at(col.asInteger)
};

~outLevelToIndex = {
	arg outputLevel;
	var x = (33/16)-(~outLevelToTL.value(outputLevel)/8);
	var modIndex = pi*(2**x);
	modIndex.postln;
	modIndex
};
)

////////////////
// algorithms //
////////////////

(
SynthDef(\alg1, {
	arg	outBus=0, freq=440, master=0.05, gate=1.0, ratios=#[1,1,1,1], indexes=#[0,0,0,0], lfo=#[0.1,0.1,0.01,0.01,0];

	var env1 = Env(NamedControl.kr(\levels1, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env2 = Env(NamedControl.kr(\levels2, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env3 = Env(NamedControl.kr(\levels3, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env4 = Env(NamedControl.kr(\levels4, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));

	var pm_speed = lfo.at(0);
	var am_speed = lfo.at(1);
	var pmd = lfo.at(2);
	var amd = lfo.at(3);
	var waveform = lfo.at(4);

	var ampMod = switch(waveform,
		0, {LFPulse.ar(am_speed,0,0.5,amd)},
		1, {LFTri.ar(am_speed,0,amd)},
		2, {LFCub.ar(am_speed,0,amd)},
		3, {LFSaw.ar(am_speed,0,amd)},
		{SinOsc.ar(am_speed, 0, amd)}
	);

	var pitchMod = switch(waveform,
		0, {LFPulse.ar(pm_speed,0,0.5,pmd)},
		1, {LFTri.ar(pm_speed,0,pmd)},
		2, {LFCub.ar(pm_speed,0,pmd)},
		3, {LFSaw.ar(pm_speed,0,pmd)},
		 {SinOsc.ar(pm_speed, 0,pmd)}
	);

	var ratio1 = ratios.at(0);
	var ratio2 = ratios.at(1);
	var ratio3 = ratios.at(2);
	var ratio4 = ratios.at(3);

	var freq1 = ratio1*freq;
	var freq2 = ratio2*freq;
	var freq3 = ratio3*freq;
	var freq4 = ratio4*freq;

	var index1 = indexes.at(0);
	var index2 = indexes.at(1);
	var index3 = indexes.at(2);
	var index4 = indexes.at(3);

	var op4 = SinOsc.ar(freq4, 0, freq4*index4) * EnvGen.kr(env4, gate, doneAction: 2);
	var op3 = SinOsc.ar(freq3 + op4, 0, freq3*index3) * EnvGen.kr(env3, gate, doneAction: 2);
	var op2 = SinOsc.ar(freq2 + op3, 0, freq2*index2) * EnvGen.kr(env2, gate, doneAction: 2);
	var op1 = SinOsc.ar(freq1 + op2 + pitchMod , 0, index1 + ampMod) * EnvGen.kr(env1, gate, doneAction: 2);

	Out.ar(0, op1*master);
}).send;
)

(
SynthDef(\alg2, {
	arg
	outBus=0, freq=440, master=0.05, gate=1.0,
	ratios=#[1,1,1,1], indexes=#[0,0,0,0], lfo=#[0.1,0.1,0.01,0.01,0];

	var pm_speed = lfo.at(0);
	var am_speed = lfo.at(1);
	var pmd = lfo.at(2);
	var amd = lfo.at(3);
	var waveform = lfo.at(4);

	var ampMod = switch(waveform,
		0, {LFPulse.ar(am_speed,0,0.5,amd)},
		1, {LFTri.ar(am_speed,0,amd)},
		2, {LFCub.ar(am_speed,0,amd)},
		3, {LFSaw.ar(am_speed,0,amd)},
		{SinOsc.ar(am_speed, 0, amd)}
	);

	var pitchMod = switch(waveform,
		0, {LFPulse.ar(pm_speed,0,0.5,pmd)},
		1, {LFTri.ar(pm_speed,0,pmd)},
		2, {LFCub.ar(pm_speed,0,pmd)},
		3, {LFSaw.ar(pm_speed,0,pmd)},
		 {SinOsc.ar(pm_speed, 0,pmd)}
	);

	var env1 = Env(NamedControl.kr(\levels1, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env2 = Env(NamedControl.kr(\levels2, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env3 = Env(NamedControl.kr(\levels3, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env4 = Env(NamedControl.kr(\levels4, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));

	var ratio1 = ratios.at(0);
	var ratio2 = ratios.at(1);
	var ratio3 = ratios.at(2);
	var ratio4 = ratios.at(3);

	var freq1 = ratio1*freq;
	var freq2 = ratio2*freq;
	var freq3 = ratio3*freq;
	var freq4 = ratio4*freq;

	var index1 = indexes.at(0);
	var index2 = indexes.at(1);
	var index3 = indexes.at(2);
	var index4 = indexes.at(3);

	var op4 = SinOsc.ar(freq4, 0, freq4*index4) * EnvGen.kr(env4, gate, doneAction: 2);
	var op3 = SinOsc.ar(freq3, 0, freq3*index3) * EnvGen.kr(env3, gate, doneAction: 2);
	var op2 = SinOsc.ar(freq2 + op3 + op4, 0, freq2*index2) * EnvGen.kr(env2, gate, doneAction: 2);
	var op1 = SinOsc.ar(freq1 + op2 + pitchMod, 0, index1 + ampMod) * EnvGen.kr(env1, gate, doneAction: 2);

	Out.ar(0, op1*master);
}).send;
)

(
SynthDef(\alg3, {
	arg
	outBus=0, freq=440, master=0.05, gate=1.0,
	ratios=#[1,1,1,1], indexes=#[0,0,0,0], lfo=#[0.1,0.1,0.01,0.01,0];

	var pm_speed = lfo.at(0);
	var am_speed = lfo.at(1);
	var pmd = lfo.at(2);
	var amd = lfo.at(3);
	var waveform = lfo.at(4);

	var ampMod = switch(waveform,
		0, {LFPulse.ar(am_speed,0,0.5,amd)},
		1, {LFTri.ar(am_speed,0,amd)},
		2, {LFCub.ar(am_speed,0,amd)},
		3, {LFSaw.ar(am_speed,0,amd)},
		{SinOsc.ar(am_speed, 0, amd)}
	);

	var pitchMod = switch(waveform,
		0, {LFPulse.ar(pm_speed,0,0.5,pmd)},
		1, {LFTri.ar(pm_speed,0,pmd)},
		2, {LFCub.ar(pm_speed,0,pmd)},
		3, {LFSaw.ar(pm_speed,0,pmd)},
		 {SinOsc.ar(pm_speed, 0,pmd)}
	);


	var env1 = Env(NamedControl.kr(\levels1, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env2 = Env(NamedControl.kr(\levels2, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env3 = Env(NamedControl.kr(\levels3, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env4 = Env(NamedControl.kr(\levels4, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));

	var ratio1 = ratios.at(0);
	var ratio2 = ratios.at(1);
	var ratio3 = ratios.at(2);
	var ratio4 = ratios.at(3);

	var freq1 = ratio1*freq;
	var freq2 = ratio2*freq;
	var freq3 = ratio3*freq;
	var freq4 = ratio4*freq;

	var index1 = indexes.at(0);
	var index2 = indexes.at(1);
	var index3 = indexes.at(2);
	var index4 = indexes.at(3);

	var op4 = SinOsc.ar(freq4, 0, freq4*index4) * EnvGen.kr(env4, gate, doneAction: 2);
	var op3 = SinOsc.ar(freq3, 0, freq3*index3) * EnvGen.kr(env3, gate, doneAction: 2);
	var op2 = SinOsc.ar(freq2 + op3, 0, freq2*index2) * EnvGen.kr(env2, gate, doneAction: 2);
	var op1 = SinOsc.ar(freq1 + op2 + op4 + pitchMod, 0, index1 + ampMod) * EnvGen.kr(env1, gate, doneAction: 2);

	Out.ar(0, op1*master);
}).send;
)

(
SynthDef(\alg4, {
	arg
	outBus=0, freq=440, master=0.05, gate=1.0,
	ratios=#[1,1,1,1], indexes=#[0,0,0,0], lfo=#[0.1,0.1,0.01,0.01,0];

	var pm_speed = lfo.at(0);
	var am_speed = lfo.at(1);
	var pmd = lfo.at(2);
	var amd = lfo.at(3);
	var waveform = lfo.at(4);

	var ampMod = switch(waveform,
		0, {LFPulse.ar(am_speed,0,0.5,amd)},
		1, {LFTri.ar(am_speed,0,amd)},
		2, {LFCub.ar(am_speed,0,amd)},
		3, {LFSaw.ar(am_speed,0,amd)},
		{SinOsc.ar(am_speed, 0, amd)}
	);

	var pitchMod = switch(waveform,
		0, {LFPulse.ar(pm_speed,0,0.5,pmd)},
		1, {LFTri.ar(pm_speed,0,pmd)},
		2, {LFCub.ar(pm_speed,0,pmd)},
		3, {LFSaw.ar(pm_speed,0,pmd)},
		 {SinOsc.ar(pm_speed, 0,pmd)}
	);

	var env1 = Env(NamedControl.kr(\levels1, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env2 = Env(NamedControl.kr(\levels2, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env3 = Env(NamedControl.kr(\levels3, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env4 = Env(NamedControl.kr(\levels4, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));

	var ratio1 = ratios.at(0);
	var ratio2 = ratios.at(1);
	var ratio3 = ratios.at(2);
	var ratio4 = ratios.at(3);

	var freq1 = ratio1*freq;
	var freq2 = ratio2*freq;
	var freq3 = ratio3*freq;
	var freq4 = ratio4*freq;

	var index1 = indexes.at(0);
	var index2 = indexes.at(1);
	var index3 = indexes.at(2);
	var index4 = indexes.at(3);

	var op4 = SinOsc.ar(freq4, 0, freq4*index4) * EnvGen.kr(env4, gate, doneAction: 2);
	var op3 = SinOsc.ar(freq3 + op4, 0, freq3*index3) * EnvGen.kr(env3, gate, doneAction: 2);
	var op2 = SinOsc.ar(freq2, 0, freq2*index2) * EnvGen.kr(env2, gate, doneAction: 2);
	var op1 = SinOsc.ar(freq1 + op2 + op3 + pitchMod, 0, index1 + ampMod) * EnvGen.kr(env1, gate, doneAction: 2);

	Out.ar(0, op1*master);
}).send;
)

(
SynthDef(\alg5, {
	arg
	outBus=0, freq=440, master=0.05, gate=1.0,
	ratios=#[1,1,1,1], indexes=#[0,0,0,0], lfo=#[0.1,0.1,0.01,0.01,0];

	var pm_speed = lfo.at(0);
	var am_speed = lfo.at(1);
	var pmd = lfo.at(2);
	var amd = lfo.at(3);
	var waveform = lfo.at(4);

	var ampMod = switch(waveform,
		0, {LFPulse.ar(am_speed,0,0.5,amd)},
		1, {LFTri.ar(am_speed,0,amd)},
		2, {LFCub.ar(am_speed,0,amd)},
		3, {LFSaw.ar(am_speed,0,amd)},
		{SinOsc.ar(am_speed, 0, amd)}
	);

	var pitchMod = switch(waveform,
		0, {LFPulse.ar(pm_speed,0,0.5,pmd)},
		1, {LFTri.ar(pm_speed,0,pmd)},
		2, {LFCub.ar(pm_speed,0,pmd)},
		3, {LFSaw.ar(pm_speed,0,pmd)},
		 {SinOsc.ar(pm_speed, 0,pmd)}
	);

	var env1 = Env(NamedControl.kr(\levels1, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env2 = Env(NamedControl.kr(\levels2, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env3 = Env(NamedControl.kr(\levels3, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env4 = Env(NamedControl.kr(\levels4, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));

	var ratio1 = ratios.at(0);
	var ratio2 = ratios.at(1);
	var ratio3 = ratios.at(2);
	var ratio4 = ratios.at(3);

	var freq1 = ratio1*freq;
	var freq2 = ratio2*freq;
	var freq3 = ratio3*freq;
	var freq4 = ratio4*freq;

	var index1 = indexes.at(0);
	var index2 = indexes.at(1);
	var index3 = indexes.at(2);
	var index4 = indexes.at(3);

	var op4 = SinOsc.ar(freq4, 0, freq4*index4) * EnvGen.kr(env4, gate, doneAction: 2);
	var op3 = SinOsc.ar(freq3  + op4 + pitchMod, 0, index3 + (ampMod* ( index3/(index1 + index3)))) * EnvGen.kr(env3, gate, doneAction: 2);
	var op2 = SinOsc.ar(freq2, 0, freq2*index2) * EnvGen.kr(env2, gate, doneAction: 2);
	var op1 = SinOsc.ar(freq1 + op2 + pitchMod, 0, index1 + (ampMod* ( index1/(index1 + index3)))) * EnvGen.kr(env1, gate, doneAction: 2);

	Out.ar(0, (op1+op3)*master);
}).send;
)

(
SynthDef(\alg6, {
	arg
	outBus=0, freq=440, master=0.05, gate=1.0,
	ratios=#[1,1,1,1], indexes=#[0,0,0,0], lfo=#[0.1,0.1,0.01,0.01,0];

	var pm_speed = lfo.at(0);
	var am_speed = lfo.at(1);
	var pmd = lfo.at(2);
	var amd = lfo.at(3);
	var waveform = lfo.at(4);

	var ampMod = switch(waveform,
		0, {LFPulse.ar(am_speed,0,0.5,amd)},
		1, {LFTri.ar(am_speed,0,amd)},
		2, {LFCub.ar(am_speed,0,amd)},
		3, {LFSaw.ar(am_speed,0,amd)},
		{SinOsc.ar(am_speed, 0, amd)}
	);

	var pitchMod = switch(waveform,
		0, {LFPulse.ar(pm_speed,0,0.5,pmd)},
		1, {LFTri.ar(pm_speed,0,pmd)},
		2, {LFCub.ar(pm_speed,0,pmd)},
		3, {LFSaw.ar(pm_speed,0,pmd)},
		 {SinOsc.ar(pm_speed, 0,pmd)}
	);

	var env1 = Env(NamedControl.kr(\levels1, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env2 = Env(NamedControl.kr(\levels2, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env3 = Env(NamedControl.kr(\levels3, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env4 = Env(NamedControl.kr(\levels4, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));

	var ratio1 = ratios.at(0);
	var ratio2 = ratios.at(1);
	var ratio3 = ratios.at(2);
	var ratio4 = ratios.at(3);

	var freq1 = ratio1*freq;
	var freq2 = ratio2*freq;
	var freq3 = ratio3*freq;
	var freq4 = ratio4*freq;

	var index1 = indexes.at(0);
	var index2 = indexes.at(1);
	var index3 = indexes.at(2);
	var index4 = indexes.at(3);

	var op4 = SinOsc.ar(freq4, 0, freq4*index4) * EnvGen.kr(env4, gate, doneAction: 2);
	var op3 = SinOsc.ar(freq3 + op4 + pitchMod, 0, index3 + (ampMod* ( index3 /(index1 + index2 + index3)))) * EnvGen.kr(env3, gate, doneAction: 2);
	var op2 = SinOsc.ar(freq2 + op4 + pitchMod, 0, index2 + (ampMod* ( index2 /(index1 + index2 + index3)))) * EnvGen.kr(env2, gate, doneAction: 2);
	var op1 = SinOsc.ar(freq1 + op4 + pitchMod, 0, index1 + (ampMod* ( index1 /(index1 + index2 + index3)))) * EnvGen.kr(env1, gate, doneAction: 2);

	Out.ar(0, (op1+op2+op3)*master);
}).send;
)

(
SynthDef(\alg7, {
	arg
	outBus=0, freq=440, master=0.05, gate=1.0,
	ratios=#[1,1,1,1], indexes=#[0,0,0,0], lfo=#[0.1,0.1,0.01,0.01,0];

	var pm_speed = lfo.at(0);
	var am_speed = lfo.at(1);
	var pmd = lfo.at(2);
	var amd = lfo.at(3);
	var waveform = lfo.at(4);

	var ampMod = switch(waveform,
		0, {LFPulse.ar(am_speed,0,0.5,amd)},
		1, {LFTri.ar(am_speed,0,amd)},
		2, {LFCub.ar(am_speed,0,amd)},
		3, {LFSaw.ar(am_speed,0,amd)},
		{SinOsc.ar(am_speed, 0, amd)}
	);

	var pitchMod = switch(waveform,
		0, {LFPulse.ar(pm_speed,0,0.5,pmd)},
		1, {LFTri.ar(pm_speed,0,pmd)},
		2, {LFCub.ar(pm_speed,0,pmd)},
		3, {LFSaw.ar(pm_speed,0,pmd)},
		 {SinOsc.ar(pm_speed, 0,pmd)}
	);

	var env1 = Env(NamedControl.kr(\levels1, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env2 = Env(NamedControl.kr(\levels2, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env3 = Env(NamedControl.kr(\levels3, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env4 = Env(NamedControl.kr(\levels4, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));

	var ratio1 = ratios.at(0);
	var ratio2 = ratios.at(1);
	var ratio3 = ratios.at(2);
	var ratio4 = ratios.at(3);

	var freq1 = ratio1*freq;
	var freq2 = ratio2*freq;
	var freq3 = ratio3*freq;
	var freq4 = ratio4*freq;

	var index1 = indexes.at(0);
	var index2 = indexes.at(1);
	var index3 = indexes.at(2);
	var index4 = indexes.at(3);

	var op4 = SinOsc.ar(freq4, 0, freq4*index4) * EnvGen.kr(env4, gate, doneAction: 2);
	var op3 = SinOsc.ar(freq3 + op4 + pitchMod, 0, index3 + (ampMod*( index3 /(index1 + index2 + index3)))) * EnvGen.kr(env3, gate, doneAction: 2);
	var op2 = SinOsc.ar(freq2 + pitchMod, 0, index2 + (ampMod* ( index2 /(index1 + index2 + index3)))) * EnvGen.kr(env2, gate, doneAction: 2);
	var op1 = SinOsc.ar(freq1 + pitchMod, 0, index1 + (ampMod* ( index1 /(index1 + index2 + index3 )))) * EnvGen.kr(env1, gate, doneAction: 2);

	Out.ar(0, (op1+op2+op3)*master);
}).send;
)

(
SynthDef(\alg8, {
	arg
	outBus=0, freq=440, master=0.05, gate=1.0,
	ratios=#[1,1,1,1], indexes=#[0,0,0,0], lfo=#[0.1,0.1,0.01,0.01,0];

	var pm_speed = lfo.at(0);
	var am_speed = lfo.at(1);
	var pmd = lfo.at(2);
	var amd = lfo.at(3);
	var waveform = lfo.at(4);

	var ampMod = switch(waveform,
		0, {LFPulse.ar(am_speed,0,0.5,amd)},
		1, {LFTri.ar(am_speed,0,amd)},
		2, {LFCub.ar(am_speed,0,amd)},
		3, {LFSaw.ar(am_speed,0,amd)},
		{SinOsc.ar(am_speed, 0, amd)}
	);

	var pitchMod = switch(waveform,
		0, {LFPulse.ar(pm_speed,0,0.5,pmd)},
		1, {LFTri.ar(pm_speed,0,pmd)},
		2, {LFCub.ar(pm_speed,0,pmd)},
		3, {LFSaw.ar(pm_speed,0,pmd)},
		 {SinOsc.ar(pm_speed, 0,pmd)}
	);

	var env1 = Env(NamedControl.kr(\levels1, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env2 = Env(NamedControl.kr(\levels2, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env3 = Env(NamedControl.kr(\levels3, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));
	var env4 = Env(NamedControl.kr(\levels4, Array.fill(33, {0})), NamedControl.kr(\times, Array.fill(32, {0})));

	var ratio1 = ratios.at(0);
	var ratio2 = ratios.at(1);
	var ratio3 = ratios.at(2);
	var ratio4 = ratios.at(3);

	var freq1 = ratio1*freq;
	var freq2 = ratio2*freq;
	var freq3 = ratio3*freq;
	var freq4 = ratio4*freq;

	var index1 = indexes.at(0);
	var index2 = indexes.at(1);
	var index3 = indexes.at(2);
	var index4 = indexes.at(3);

	var op4 = SinOsc.ar(freq4 + pitchMod, 0, index4 + (ampMod* (index4 /(index1 + index2 + index3 + index4) )) ) * EnvGen.kr(env4, gate, doneAction: 2);
	var op3 = SinOsc.ar(freq3 + pitchMod, 0, index3 + (ampMod* (index3 /(index1 + index2 + index3 + index4) )) ) * EnvGen.kr(env3, gate, doneAction: 2);
	var op2 = SinOsc.ar(freq2 + pitchMod, 0, index2 + (ampMod* (index2 /(index1 + index2 + index3 + index4) )) ) * EnvGen.kr(env2, gate, doneAction: 2);
	var op1 = SinOsc.ar(freq1 + pitchMod, 0, index1 + (ampMod* (index1 / (index1 + index2 + index3 + index4) )) ) * EnvGen.kr(env1, gate, doneAction: 2);

	Out.ar(0, (op1+op2+op3+op4)*master);
}).send;
)

//////////
// GUI //
//////////

(
var win, background_win, font, labelColor, algorithms, algorithmImages, algorithm_subwin, algorithm_subwin_image, algorithmButtons, knob_size, knob_outline_size, stroke_size, slider, value, r, clicked, relativeWhere, multiSlider_envelope, envelope, imagePath, scope_win, scope, knobs, knobLFO_win, knobLFO, waveLFO, wave_images, counter, knobMater, ratio, outputLvl, synth, time , time_array, time_step, time_control, vol_array, envs, indexes, notes, on, off, ratios, outLvls, currentAlg=\alg1, knob_val, master_vol, knobVal_win, knobVal_text;

algorithms = [
	\alg1,
	\alg2,
	\alg3,
	\alg4,
	\alg5,
	\alg6,
	\alg7,
	\alg8,
];

//////////
// MIDI //
//////////

MIDIClient.init;
MIDIIn.connectAll;

notes = Array.newClear(128);

on = MIDIFunc.noteOn({
	|veloc, num, chan, src|
	/*
	var freq = num.midicps;
	var alg = ~algorithms.at(a);
	var sigfac = [0,0,0,0];
	sigfac.put(0,alg.at(4));
	sigfac.put(1,alg.at(9));
	sigfac.put(2,alg.at(14));
	sigfac.put(3,alg.at(19));
	if(alg.at(4)==0) {alg.put(4,freq*ratios.at(0))};
	if(alg.at(9)==0) {alg.put(9,freq*ratios.at(1))};
	if(alg.at(14)==0) {alg.put(14,freq*ratios.at(2))};
	if(alg.at(19)==0) {alg.put(19,freq*ratios.at(3))};
	*/
	time_array.postln;
	notes[num] = Synth(currentAlg, [
		\freq, num.midicps,
		\gate, 1,
		\ratios, ratios,
		\indexes, indexes,
		\levels1, envs.at(0),
		\levels2, envs.at(1),
		\levels3, envs.at(2),
		\levels4, envs.at(3),
		\times, time_array,
		\lfo, knob_val,
		\master, master_vol
	]);
});

off = MIDIFunc.noteOff({
	|veloc, num, chan, src|
	notes[num].release;
});

q = { on.free; off.free; };

font = Font("Agency FB", 30, bold: true);
labelColor = Color.new255(12,167,137);
knob_size = 8;
knob_outline_size = 15;
stroke_size = 2;
value = [0,0];
r = { Rect(325.rand, 325.rand, 25, 25) } ! 4;
r.class.postln;
imagePath = PathName(thisProcess.nowExecutingPath).parentPath;
ratios = [0, 0, 0, 0];
indexes = [0, 0, 0, 0];


Window.closeAll;
//here you can find all the path needed for the backgrounds (attention to the fact that probably you need to recall it
background_win= Image.new(imagePath ++ "/vintage-retro-old-wood-texture-2866500.jpg");
win = Window.new("FMSynth", Rect(0,0,1000,600) ,false);
win.view.setBackgroundImage(background_win);

//////////////////////////////////////
////////////title/////////////////////
//////////////////////////////////////

StaticText(win, Rect(279,4,300,50))
.string_("WOODY")
.font_(Font("Woodwarrior", 52, bold: true))
.stringColor_(Color.white)
.align_(\center);

StaticText(win, Rect(280,5,300,50))
.string_("WOODY")
.font_(Font("Woodwarrior", 50, bold: true))
.stringColor_(Color.new255(192,64,0))
.align_(\center);

StaticText(win,Rect(569, 5, 100, 55))
.string_("FMSYNTH")
.font_(Font("Agency FB", 25, bold: true))
.stringColor_(Color.new255(192,64,0))
.align_(\bottomLeft);

StaticText(win,Rect(570, 5, 100, 55))
.string_("FMSYNTH")
.font_(Font("Agency FB", 25, bold: true))
.stringColor_(Color.white)
.align_(\bottomLeft);


//////////////////////////////////////
//////////////Algolirthms/////////////
//////////////////////////////////////


algorithm_subwin = FlowView.new( win, Rect(20, 60, 270, 370), 0@0, 12@10 );
algorithm_subwin_image = FlowView.new( win, Rect(20, 190, 250, 150), 0@0, 10@10 );
algorithm_subwin_image.view.setBackgroundImage(Image.new(imagePath ++ "/Algorithm1(250x150).jpg"));


StaticText(win, Rect(20, 140 ,250, 38))
	.string_("ALGORITHM")
	.font_(font)
    .stringColor_(Color.white)
    .align_(\center);


algorithmImages = [
	Image.new(imagePath ++ "/Algorithm1(250x150).jpg"),
	Image.new(imagePath ++ "/Algorithm2(250x150).jpg"),
	Image.new(imagePath ++ "/Algorithm3(250x150).jpg"),
	Image.new(imagePath ++ "/Algorithm4(250x150).jpg"),
	Image.new(imagePath ++ "/Algorithm5(250x150).jpg"),
	Image.new(imagePath ++ "/Algorithm6(250x150).jpg"),
	Image.new(imagePath ++ "/Algorithm7(250x150).jpg"),
	Image.new(imagePath ++ "/Algorithm8(250x150).jpg");
];

algorithmButtons = Array.fill(8, {arg i;
		Button(algorithm_subwin, 51@30)
	.states_([[(i+1).asString, Color.white, Color.black]])
	    .action_({
		// qui dentro inserire la funzione che deve svolgere ogni bottone
		algorithm_subwin_image.view.setBackgroundImage(algorithmImages[i]);
		algorithmButtons.size.do({arg button;
			if(button!=i,{algorithmButtons[button].states_([[(button+1).asString, Color.white, Color.black]])},                        {algorithmButtons[button].states_([[(button+1).asString, Color.white, labelColor]])})
        });
		currentAlg = algorithms.at(i);
	});
});


/////////////////////////////////
/////////// slider///////////////
/////////////////////////////////

slider = UserView( win, Rect( 300, 60, 350 , 350 ));

slider.drawFunc = {
	Pen.width = stroke_size;

	// draw the background
	Pen.addRect( Rect(0,0, slider.bounds.width,slider.bounds.height) );
	Pen.fillColor_(Color.white);
	Pen.fill;
	"OUTPUT LEVEL".do({|char,i|
		char.asString.drawCenteredIn(Rect(3,(i*16 + 70),16,16), Font("Agency FB", 15, bold: true), Color.black);
	});

	"F R E Q U E N C Y   L E V E L".drawCenteredIn(Rect(0,330,350,20), Font("Agency FB", 15, bold: true), Color.black);

	//draw the sliders
	r.do { arg x,i;
        Pen.addOval(x); // wie addRect
        Pen.color = Color.black;
        Pen.draw;
		(i+1).asString.drawCenteredIn(x, Font("Agency FB", 10, bold: true), Color.white );
    };

	//drow the frame border
	Pen.addRect( Rect(0,0, slider.bounds.width,slider.bounds.height) );
	Pen.strokeColor_(Color.black);
	Pen.stroke;
};
slider.refresh;

/*
// Set the default action
slider.action = {
	synth.set(\amp, 1 - value[1]); // Requires to invert the value, as the Y axis from GUI goes from top to bottom, and sliders usually goes from bottom to top.
	synth.set(\amount, linexp(value[0], 0, 1, synth_amount_range[0], synth_amount_range[1])); // Exponential mapping between the 0 -> 1 value and the amount range. Change this settings according to your needs.

	slider.refresh // Call the drawFunc of the slider to update graphics
};
*/

slider.mouseDownAction = { |v, x, y|
    r.do { |rect, i|
        if(rect.contains(Point(x, y))) {
            clicked = i;
            relativeWhere = Point(x, y) - rect.origin;
        };
    };
};

ratio = [
	\ratio1,
	\ratio2,
	\ratio3,
	\ratio4,
];

outputLvl = [
	\outLvl1,
	\outLvl2,
	\outLvl3,
	\outLvl4,
];

// Define mouse actions
slider.mouseMoveAction = { arg  v, x, y;
    var rect;
    if(clicked.notNil) {
		if( Rect(0,0, slider.bounds.width,slider.bounds.height).contains(Point(x,y))){
			rect = r.at(clicked);
			r.put(clicked, rect.origin = Point(x, y) - relativeWhere);
			ratios[clicked] = r.at(clicked).center.x/slider.bounds.width*15;
			indexes[clicked] = ~outLevelToIndex.value(99 - (r.at(clicked).center.y/slider.bounds.height*99));
			ratios.postln;
			indexes.postln;
			slider.refresh;
	}}
};

slider.mouseUpAction = {
    clicked = nil;
};



///////////////////////////////////
//////////////envelope/////////////
///////////////////////////////////

envelope = FlowView(win, Rect(680, 60, 300, 370), 0@0, 10@10);
multiSlider_envelope = Array.fill(4, {arg i;
	MultiSliderView(envelope, 300@80)
	.value_(Array.fill(32,{0.0}))
	.isFilled_(true)
	.indexThumbSize_(8.3)
	.gap_(1.4)
	.colors_(Color.new255(192,64,0), Color.new255(192,64,0))
	.action_({ |x|
		envs.put(i, x.value.addFirst(0.1));
	});
});

///////////////////////////////////
/////////////scope/////////////////
///////////////////////////////////

scope_win = FlowView.new( win, Rect(300, 430, 350, 150), 0@0, 0@0 );
scope = FreqScopeView(scope_win, scope_win.view.bounds);
scope.active_(true); // turn it on the first time;



///////////////////////////////////
////////////LFO////////////////////
///////////////////////////////////

knobs = [
  ["P SPEED", 0.1, 20],
  ["A SPEED", 0.1, 20],
  ["PMD", 0.01, 20],
  ["AMD", 0.01, 1],
];

knob_val = [0.1, 0.1, 0.01, 0.01, 0];

wave_images = [
  Image.new(imagePath ++ "/Wave1.jpg"),
  Image.new(imagePath ++ "/Wave2.jpg"),
  Image.new(imagePath ++ "/Wave3.jpg"),
  Image.new(imagePath ++ "/Wave4.jpg"),
];

StaticText(win, Rect(20, 360 ,250, 60))
  .string_("LFO")
    .font_(Font("Agency FB", 60, bold: true))
    .stringColor_(Color.white)
    .align_(\bottomLeft);

counter = 0;

waveLFO = Button(win, Rect(150, 360, 100, 52))
.states_([["",Color.white,Color.white]])
.icon_(Image.new(imagePath ++ "/Wave1.jpg"))
.iconSize_(90)
.action_({
  if(counter==4){counter = 0};
  waveLFO.icon = wave_images[counter];
	knob_val[4] = counter;
  counter = counter + 1;
});

knobVal_win =  FlowView.new( win, Rect(35, 492, 200, 120), 0@0, 98@52 );
knobLFO_win =  FlowView.new( win, Rect(20, 450, 250, 150), 0@0, 15@25 );


knobVal_text = Array.fill(4,{ arg i;
  StaticText(knobVal_win, 30@20)
  .string_(knob_val[i].asString)
    .font_(Font("Agency FB", 18, bold: true))
    .stringColor_(Color.white)
    .align_(\bottomLeft);
});


knobLFO = Array.fill(4, {arg i;
  Knob(knobLFO_win, 45@45)
  .mode_(\vert)
  .color_([Color.black, labelColor, Color.white, labelColor])
  .action_({
    arg x;
    knob_val[i] = x.value * (knobs[i][2]- knobs[i][1]) + knobs[i][1];
    knobVal_text[i].string = knob_val[i].asString;

  });
  StaticText(knobLFO_win, 52@45)
  .string_(knobs[i][0])
    .font_(Font("Agency FB", 18, bold: true))
    .stringColor_(Color.white)
    .align_(\bottomLeft);

});

/////////////////////////////////////
//////////time///////////////////////
/////////////////////////////////////

time_control = ControlSpec.new(minval: 0.1, maxval: 30, warp: 'lin', step: 0.1, default: 3, units: "s");

time = EZSlider.new(win, Rect(680,430,300,30),"TIME", time_control,  labelWidth: 40)
.setColors(
	Color.clear, //stringBackground
	Color.white, //stringColor
	Color.white, //sliderBackground
	Color.black, //numBackground
	Color.white, //numStringColor
	Color.white, //numNormalColor
	Color.white, //numTypingColor
	Color.new255(192,64,0),//knobColor
	Color.clear,

)
.font_(Font("Agency FB", 20, bold: true))
.action_({|x|
	//x.value.postln;
	time_step = x.value/32;
	time_array = Array.fill(32, {  time_step });
});
time_step = time.value/32;
time_array = Array.fill(32, {  time_step });

envs =[
  multiSlider_envelope.at(0).value.addFirst(0.1),
  multiSlider_envelope.at(1).value.addFirst(0.1),
  multiSlider_envelope.at(2).value.addFirst(0.1),
  multiSlider_envelope.at(3).value.addFirst(0.1)
];

///////////////////////////////////////
///////////master//////////////////////
///////////////////////////////////////

knobMater = Knob(win,Rect (680,480, 100, 100))
.mode_(\vert)
.color_([Color.black, Color.new255(192,64,0) ,Color.white, Color.new255(192,64,0)])
.action_({
  arg x;
  master_vol = x.value * 0.1;
  master_vol.postln;
  });
StaticText(win, Rect(790,480,200,100))
.string_("MASTER")
.font_(Font("Agency FB", 45, bold: true))
.stringColor_(Color.white)
.align_(\bottomLeft);

/////////////////////////////////////
////////control on close/////////////
/////////////////////////////////////

win.onClose_({ scope.kill }); // you must have this

win.onClose = {background_win.free; algorithmImages.do{|x| x.free}; wave_images.do{|x| x.free}};
win.front;

)

MIDIFunc.trace(bool: true);

q.value;

s.queryAllNodes;
s.quit;