// FM polysynth engine
// Polyphony code is from MxSynths by infinitedigits @schollz

// this is a CroneEngine
Engine_xD1 : CroneEngine{
  var endOfChain;
  var outBus;
  var xParameters;
  var xVoices;
  var xVoicesOn;
  var fnNoteOn, fnNoteOnMono, fnNoteOnPoly, fnNoteAdd;
  var fnNoteOff, fnNoteOffMono, fnNoteOffPoly;
  var pedalSustainOn=false;
  var pedalSostenutoOn=false;
  var pedalSustainNotes;
  var pedalSostenutoNotes;
  var polyphonyMax=20;
  var polyphonyCount=0;

  *new { arg context, doneCallback;
    ^super.new(context, doneCallback);
  }

  alloc {
    outBus = Bus.audio;
    SynthDef("ColorLimiter", { arg input;
        Out.ar(context.out_b, In.ar(input).tanh.dup);
      }).add;
    Server.default.sync;
    endOfChain = Synth.new("ColorLimiter", [\input, outBus]);
    NodeWatcher.register(endOfChain);

    xParameters = Dictionary.with(*[
      "amp"->0.5, "monophonic"->0, "alg"->0,
      "num1"->1, "num2"->1, "num3"->1, "num4"->1, "num5"->1, "num6"->1,
      "denom1"->1, "denom2"->1, "denom3"->1, "denom4"->1, "denom5"->1, "denom6"->1,
      "hirat"->0.125, "lorat"->8, "hires"->0, "lores"->0,
      "oamp1"->1, "oamp2"->1, "oamp3"->1, "oamp4"->1, "oamp5"->1, "oamp6"->1,
      "oatk1"->0.1, "oatk2"->0.1, "oatk3"->0.1, "oatk4"->0.1, "oatk5"->0.1, "oatk6"->0.1, "fatk"->0.1, "patk"->0.1,
      "odec1"->0.3, "odec2"->0.3, "odec3"->0.3, "odec4"->0.3, "odec5"->0.3, "odec6"->0.3, "fdec"->0.3, "pdec"->0.3,
      "osus1"->0.7, "osus2"->0.7, "osus3"->0.7, "osus4"->0.7, "osus5"->0.7, "osus6"->0.7, "fsus"->0.7, "psus"->0.7,
      "orel1"->0.2, "orel2"->0.2, "orel3"->0.2, "orel4"->0.2, "orel5"->0.2, "orel6"->0.2, "frel"->0.2, "prel"->0.2,
      "hfamt"->0, "lfamt"->1, "pamt"->0,
      "ocurve"->(-1.0), "fcurve"->(-1.0), "pcurve"->0,
      "lfreq"->1, "lfade"->0, "lfo_am"->0, "lfo_pm"->0, "lfo_hfm"->0, "lfo_lfm"->0, "feedback"->0
      ]);
    xVoices = Dictionary.new;
    xVoicesOn = Dictionary.new;
    pedalSustainNotes = Dictionary.new;
    pedalSostenutoNotes = Dictionary.new;

    32.do({ arg i; SynthDef(("xD1_"++i).asString, {
        arg out, note=69, amp=1, gate=0,
        num1=1, num2=1, num3=1, num4=1, num5=1, num6=1,
        denom1=1, denom2=1, denom3=1, denom4=1, denom5=1, denom6=1,
        hirat=0.125, lorat=8, hires=0, lores=0,
        oamp1=1, oamp2=1, oamp3=1, oamp4=1, oamp5=1, oamp6=1,
        oatk1=0.1, oatk2=0.1, oatk3=0.1, oatk4=0.1, oatk5=0.1, oatk6=0.1, fatk=0.1, patk=0.1,
        odec1=0.3, odec2=0.3, odec3=0.3, odec4=0.3, odec5=0.3, odec6=0.3, fdec=0.3, pdec=0.3,
        osus1=0.7, osus2=0.7, osus3=0.7, osus4=0.7, osus5=0.7, osus6=0.7, fsus=0.7, psus=0.7,
        orel1=0.2, orel2=0.2, orel3=0.2, orel4=0.2, orel5=0.2, orel6=0.2, frel=0.2, prel=0.2,
        hfamt=0, lfamt=1, pamt=0,
        ocurve=(-1.0), fcurve=(-1.0), pcurve=0,
        lfreq=1, lfade=0, lfo_am=0, lfo_pm=0, lfo_hfm=0, lfo_lfm=0, feedback=0;

        var maxrel = ArrayMax.kr([orel1, orel2, orel3, orel4, orel5, orel6])[0];
        var menv = Env.asr(0, 1, maxrel).kr(2, gate);
        var fenv = Env.adsr(fatk, fdec, fsus, frel, 1, fcurve).kr(0, gate);
        var penv = Env.adsr(patk, pdec, psus, prel, pamt, pcurve).kr(0, gate);
        var oenv1 = Env.adsr(oatk1, odec1, osus1, orel1, 1, ocurve).kr(0, gate);
        var oenv2 = Env.adsr(oatk2, odec2, osus2, orel2, 1, ocurve).kr(0, gate);
        var oenv3 = Env.adsr(oatk3, odec3, osus3, orel3, 1, ocurve).kr(0, gate);
        var oenv4 = Env.adsr(oatk4, odec4, osus4, orel4, 1, ocurve).kr(0, gate);
        var oenv5 = Env.adsr(oatk5, odec5, osus5, orel5, 1, ocurve).kr(0, gate);
        var oenv6 = Env.adsr(oatk6, odec6, osus6, orel6, 1, ocurve).kr(0, gate);
        var lfo = LFTri.kr(lfreq, mul:Env.asr(lfade, 1, 10).kr(0, gate));
        var alfo = lfo.madd(0.05, 1.0) * lfo_am;
        var pitch = (note + (1.2 * lfo_pm * lfo) + (1.2 * penv)).midicps;
        var ctls = [
        [pitch * num1 / denom1, 0, (oenv1 + alfo) * oamp1],
        [pitch * num2 / denom2, 0, (oenv2 + alfo) * oamp2],
        [pitch * num3 / denom3, 0, (oenv3 + alfo) * oamp3],
        [pitch * num4 / denom4, 0, (oenv4 + alfo) * oamp4],
        [pitch * num5 / denom5, 0, (oenv5 + alfo) * oamp5],
        [pitch * num6 / denom6, 0, (oenv6 + alfo) * oamp6]
        ];
        var hifreq = hirat * (note + (1.2 * lfo_hfm * lfo) + (1.2 * fenv * hfamt)).midicps;
        var lofreq = lorat * (note + (1.2 * lfo_lfm * lfo) + (1.2 * fenv * lfamt)).midicps;
        var snd = FM7.arAlgo(i, ctls, feedback);
        snd = SVF.ar(snd, hifreq, hires, lowpass:0, highpass:1);
        snd = SVF.ar(snd, lofreq, lores);
        Out.ar(out, (snd * amp * menv));
      }).add;
    });

    fnNoteOnMono = {
      arg note, amp, duration;
      var notesOn = false;
      var setNote = false;
      xVoices.keysValuesDo({ arg key, syn;
        if (syn.isRunning, {
          notesOn = true;
        });
      });
      if (notesOn==false,{
        fnNoteOnPoly.(note,amp,duration);
      },{
        xVoices.keysValuesDo({ arg key, syn;
          if (syn.isRunning,{
            syn.set(\gate,0);
            if (setNote==false,{
              syn.set(\gate,1,
                \note,note);
              setNote = true;
            });
          });
        });
      });
      fnNoteAdd.(note);
    };

    fnNoteOnPoly = {
      arg note, amp, duration;
      var def = "xD1_0";
      32.do({ arg i;
        if (xParameters.at("alg")==i, {
          def = "xD1_" ++i.asString;
        });
      });

      if(xVoices.at(note) != nil, {
        if(xVoices.at(note).isRunning, {
            xVoices.at(note).set(\gate,0);
          }, {
            xVoices.at(note).free;
          });
      });

      xVoices.put(note,
        Synth.before(endOfChain, def, [
          \out, outBus,
          \note, note,
          \amp, amp*xParameters.at("amp"),
          \gate, 1,
          \num1, xParameters.at("num1"),
          \num2, xParameters.at("num2"),
          \num3, xParameters.at("num3"),
          \num4, xParameters.at("num4"),
          \num5, xParameters.at("num5"),
          \num6, xParameters.at("num6"),
          \denom1, xParameters.at("denom1"),
          \denom2, xParameters.at("denom2"),
          \denom3, xParameters.at("denom3"),
          \denom4, xParameters.at("denom4"),
          \denom5, xParameters.at("denom5"),
          \denom6, xParameters.at("denom6"),
          \hirat, xParameters.at("hirat"),
          \lorat, xParameters.at("lorat"),
          \oamp1, xParameters.at("oamp1"),
          \oamp2, xParameters.at("oamp2"),
          \oamp3, xParameters.at("oamp3"),
          \oamp4, xParameters.at("oamp4"),
          \oamp5, xParameters.at("oamp5"),
          \oamp6, xParameters.at("oamp6"),
          \oatk1, xParameters.at("oatk1"),
          \oatk2, xParameters.at("oatk2"),
          \oatk3, xParameters.at("oatk3"),
          \oatk4, xParameters.at("oatk4"),
          \oatk5, xParameters.at("oatk5"),
          \oatk6, xParameters.at("oatk6"),
          \fatk, xParameters.at("fatk"),
          \patk, xParameters.at("patk"),
          \odec1, xParameters.at("odec1"),
          \odec2, xParameters.at("odec2"),
          \odec3, xParameters.at("odec3"),
          \odec4, xParameters.at("odec4"),
          \odec5, xParameters.at("odec5"),
          \odec6, xParameters.at("odec6"),
          \fdec, xParameters.at("fdec"),
          \pdec, xParameters.at("pdec"),
          \osus1, xParameters.at("osus1"),
          \osus2, xParameters.at("osus2"),
          \osus3, xParameters.at("osus3"),
          \osus4, xParameters.at("osus4"),
          \osus5, xParameters.at("osus5"),
          \osus6, xParameters.at("osus6"),
          \fsus, xParameters.at("fsus"),
          \psus, xParameters.at("psus"),
          \orel1, xParameters.at("orel1"),
          \orel2, xParameters.at("orel2"),
          \orel3, xParameters.at("orel3"),
          \orel4, xParameters.at("orel4"),
          \orel5, xParameters.at("orel5"),
          \orel6, xParameters.at("orel6"),
          \frel, xParameters.at("frel"),
          \prel, xParameters.at("prel"),
          \hfamt, xParameters.at("hfamt"),
          \lfamt, xParameters.at("lfamt"),
          \pamt, xParameters.at("pamt"),
          \ocurve, xParameters.at("ocurve"),
          \fcurve, xParameters.at("fcurve"),
          \pcurve, xParameters.at("pcurve"),
          \lfreq, xParameters.at("lfreq"),
          \lfade, xParameters.at("lfade"),
          \lfo_am, xParameters.at("lfo_am"),
          \lfo_pm, xParameters.at("lfo_pm"),
          \lfo_hfm, xParameters.at("lfo_hfm"),
          \lfo_lfm, xParameters.at("lfo_lfm"),
          \feedback, xParameters.at("feedback")
          ]);
      );
      NodeWatcher.register(xVoices.at(note));
      fnNoteAdd.(note);
    };

    fnNoteAdd = {
      arg note;
      var oldestNote = 0;
      var oldestNoteVal = 10000000;
      polyphonyCount = polyphonyCount + 1;
      xVoicesOn.put(note, polyphonyCount);
      if (xVoicesOn.size > polyphonyMax, {
        xVoicesOn.keysValuesDo({ arg key, val;
          if (val < oldestNoteVal, {
            oldestNoteVal = val;
            oldestNote = key;
          });
        });
      ("max polyphony reached, removing note "++oldestNote).postln;
      fnNoteOff.(oldestNote);
      });
    };

    fnNoteOn = {
      arg note, amp, duration;
      if (xParameters.at("monophonic") > 0, {
        fnNoteOnMono.(note, amp, duration);
      },{
        fnNoteOnPoly.(note, amp, duration);
      });
    };

    fnNoteOff = {
      arg note;
      if ((xVoices.at(note)==nil) || ((xVoices.at(note).isRunning==false) && (xVoicesOn.at(note)==nil)),{},{
        if (xParameters.at("monophonic") > 0, {
          fnNoteOffMono.(note);
        },{
          fnNoteOffPoly.(note);
        });
      });
    };

    fnNoteOffMono = {
      arg note;
      var notesOn = false;
      var playedAnother = false;
      xVoicesOn.removeAt(note);
      xVoicesOn.keysValuesDo({ arg note, syn;
        notesOn=true;
      });
      if (notesOn==false,{
        xVoices.keysValuesDo({ arg note, syn;
          if (syn.isRunning, {
            syn.set(\gate, 0);
          });
        });
      },{
        xVoices.keysValuesDo({ arg note, syn;
          if (syn.isRunning, {
            syn.set(\gate, 0);
            if (playedAnother==false, {
              syn.set(\gate, 1, \note, note);
              playedAnother = true;
            });
          });
        });
      });
    };

    fnNoteOffPoly = {
      arg note;
      xVoicesOn.removeAt(note);

      if (pedalSustainOn==true,{
        pedalSustainNotes.put(note,1);
      },{
        if ((pedalSostenutoOn==true) && (pedalSostenutoNotes.at(note) != nil),{},{
          xVoices.at(note).set(\gate,0);
        });
      });
    };

    this.addCommand("note_on", "iff", { arg msg;
      var note = msg[1];
      if (xVoices.at(note)!=nil,{
        if (xVoices.at(note).isRunning==true,{
          xVoices.at(note).set(\gate,0);
        });
      });
    fnNoteOn.(msg[1], msg[2], msg[3]);
    });

    this.addCommand("note_off", "i", { arg msg;
      var note = msg[1];
      fnNoteOff.(note);
    });

    this.addCommand("sustain", "i", { arg msg;
      pedalSustainOn=(msg[1]==1);
      if(pedalSustainOn==false, {
        pedalSustainNotes.keysValuesDo({ arg note, val;
          if (xVoicesOn.at(note)==nil, {
            pedalSustainNotes.removeAt(note);
            fnNoteOff.(note);
          });
        });
      },{
        xVoicesOn.keysValuesDo({ arg note, val;
          pedalSustainNotes.put(note,1);
        });
      });
    });

    this.addCommand("sostenuto", "i", { arg msg;
      pedalSostenutoOn = (msg[1]==1);
      if (pedalSostenutoOn==false, {
        pedalSostenutoNotes.keysValuesDo({ arg note, val;
          if (xVoicesOn.at(note)==nil, {
            pedalSostenutoNotes.removeAt(note);
            fnNoteOff.(note);
          });
        });
      },{
        xVoicesOn.keysValuesDo({ arg note, val;
          pedalSostenutoNotes.put(note,1);
        });
      });
    });

    this.addCommand("set_polyphony", "i", { arg msg;
      if (msg[1] == 1, {
        xParameters.put("monophonic",1);
      },{
        xParameters.put("monophonic",0);
        polyphonyMax=msg[1];
      });
    });

    this.addCommand("killall", "", {
      Server.default.nextNodeID.do({ arg i; 
        Node.basicNew(Server.default, i).set(\gate, 0);
      });
    });

    this.addCommand("set", "sf", { arg msg;
      var key = msg[1].asString;
      var val = msg[2];
      xParameters.put(key,val);
      switch (key,
        "alg", {},
        {
          xVoices.keysValuesDo({ arg note, syn;
            if (syn.isRunning==true,{
              syn.set(key.asSymbol,val);
            });
          });
        });
    });
  }

  free {
    xVoices.keysValuesDo({ arg key, value; value.free; });
    endOfChain.free;
    outBus.free;
  }
}
