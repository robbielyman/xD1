// FM polysynth engine
// Polyphony code is from MxSynths by infinitedigits @schollz

// this is a CroneEngine
Engine_xD1 : CroneEngine{
  var Parameters;
  var Voices;
  var VoicesOn;
  var fnNoteOn, fnNoteOnMono, fnNoteOnPoly, fnNoteAdd;
  var fnNoteOff, fnNoteOffMono, fnNoteOffPoly;
  var pedalSustainOn=false;
  var pedalSostenutoOn=false;
  var pedalSustainNotes;
  var pedalSostenutoNotes;
  var PolyphonyMax=20;
  var PolyphonyCount=0;

  *new { arg context, doneCallback;
    ^super.new(context, doneCallback);
  }

  alloc {
    Parameters = Dictionary.with(*[
      "amp"->0.5, "monophonic"->0.0, "alg"->0,
      "num1"->1, "num2"->1, "num3"->1, "num4"->1, "num5"->1, "num6"->1,
      "denom1"->1, "denom2"->1, "denom3"->1, "denom4"->1, "denom5"->1, "denom6"->1,
      "hirat"->0.125, "lorat"->8,
      "oamp1"->1, "oamp2"->1, "oamp3"->1, "oamp4"->1, "oamp5"->1, "oamp6"->1,
      "oatk1"->0.1, "oatk2"->0.1, "oatk3"->0.1, "oatk4"->0.1, "oatk5"->0.1, "oatk6"->0.1, "fatk"->0.1, "patk"->0.1,
      "odec1"->0.3, "odec2"->0.3, "odec3"->0.3, "odec4"->0.3, "odec5"->0.3, "odec6"->0.3, "fdec"->0.3, "pdec"->0.3,
      "osus1"->0.7, "osus2"->0.7, "osus3"->0.7, "osus4"->0.7, "osus5"->0.7, "osus6"->0.7, "fsus"->0.7, "psus"->0.7,
      "orel1"->0.2, "orel2"->0.2, "orel3"->0.2, "orel4"->0.2, "orel5"->0.2, "orel6"->0.2, "frel"->0.2, "prel"->0.2,
      "oamt1"->1, "oamt2"->1, "oamt3"->1, "oamt4"->1, "oamt5"->1, "oamt6"->1, "hfamt"->0, "lfamt"->1, "pamt"->0,
      "ocurve"->-1, "fcurve"->-1, "pcurve"->0,
      "lfreq"->1, "lfade"->0, "lfo_am"->0, "lfo_pm"->0, "lfo_hfm"->0, "lfo_lfm"->0, "feedback"->0
      ]);
    Voices = Dictionary.new;
    VoicesOn = Dictionary.new;
    pedalSustainNotes = Dictionary.new;
    pedalSostenutoNotes = Dictionary.new;

    SynthDef("xD1", {
      arg out, note=69, amp=0.5, gate=0, alg=0,
      num1=1, num2=1, num3=1, num4=1, num5=1, num6=1,
      denom1=1, denom2=1, denom3=1, denom4=1, denom5=1, denom6=1,
      hirat=0.125, lorat=8,
      oamp1=1, oamp2=1, oamp3=1, oamp4=1, oamp5=1, oamp6=1,
      oatk1=0.1, oatk2=0.1, oatk3=0.1, oatk4=0.1, oatk5=0.1, oatk6=0.1, fatk=0.1, patk=0.1,
      odec1=0.3, odec2=0.3, odec3=0.3, odec4=0.3, odec5=0.3, odec6=0.3, fdec=0.3, pdec=0.3,
      osus1=0.7, osus2=0.7, osus3=0.7, osus4=0.7, osus5=0.7, osus6=0.7, fsus=0.7, psus=0.7,
      orel1=0.2, orel2=0.2, orel3=0.2, orel4=0.2, orel5=0.2, orel6=0.2, frel=0.2, prel=0.2,
      oamt1=1, oamt2=1, oamt3=1, oamt4=1, oamt5=1, oamt6=1, hfamt=0, lfamt=1, pamt=0,
      ocurve=-1, fcurve=-1, pcurve=0,
      lfreq=1, lfade=0, lfo_am=0, lfo_pm=0, lfo_hfm=0, lfo_lfm=0, feedback=0;

      var maxrel = [orel1, orel2, orel3, orel4, orel5, orel6].maxItem;
      var menv = Env.asr(0, 1, maxrel).kr(2, gate);
      var fenv = Env.adsr(fatk, fdec, fsus, frel, 1, fcurve).kr(0, gate);
      var penv = Env.adsr(patk, pdec, psus, prel, pamt, pcurve).kr(0, gate);
      var oenv1 = Env.adsr(oatk1, odec1, osus1, orel1, oamt1, ocurve).kr(0, gate);
      var oenv2 = Env.adsr(oatk2, odec2, osus2, orel2, oamt2, ocurve).kr(0, gate);
      var oenv3 = Env.adsr(oatk3, odec3, osus3, orel3, oamt3, ocurve).kr(0, gate);
      var oenv4 = Env.adsr(oatk4, odec4, osus4, orel4, oamt4, ocurve).kr(0, gate);
      var oenv5 = Env.adsr(oatk5, odec5, osus5, orel5, oamt5, ocurve).kr(0, gate);
      var oenv6 = Env.adsr(oatk6, odec6, osus6, orel6, oamt6, ocurve).kr(0, gate);
      var lfo = LFTri.kr(lfreq, mul:Env.asr(lfade, 1, 10).kr(0, gate));
      var alfo = plfo.madd(0.05, 1.0) * lfo_am;
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
      var snd = FM7.arAlgo(alg, ctls, feedback);
      snd = SVF.ar(snd, hifreq, hires, lowpass:0, highpass:1);
      snd = SVF.ar(snd, lofreq, lores);
      Out.ar(out, (snd * amp).dup);
    }).add;

    fnNoteOnMono = {
      arg note, amp, duration;
      var notesOn = false;
      var setNote = false;
      Voices.keysValuesDo({ arg key, syn;
        if (syn.isRunning, {
          notesOn = true;
        });
      });
      if (notesOn==false,{
        fnNoteOnPoly.(note,amp,duration);
      },{
        Voices.keysValuesDo({ arg key, syn;
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

      Voices.put(note,
        Synth.new("xD1",[
          \out, context.out_b,
          \note, note,
          \amp, amp*Parameters.at("amp"),
          \gate, 1,
          \alg, Parameters.at("alg"),
          \num1, Parameters.at("num1"),
          \num2, Parameters.at("num2"),
          \num3, Parameters.at("num3"),
          \num4, Parameters.at("num4"),
          \num5, Parameters.at("num5"),
          \num6, Parameters.at("num6"),
          \denom1, Parameters.at("denom1"),
          \denom2, Parameters.at("denom2"),
          \denom3, Parameters.at("denom3"),
          \denom4, Parameters.at("denom4"),
          \denom5, Parameters.at("denom5"),
          \denom6, Parameters.at("denom6"),
          \hirat, Parameters.at("hirat"),
          \lorat, Parameters.at("lorat"),
          \oamp1, Parameters.at("oamp1"),
          \oamp2, Parameters.at("oamp2"),
          \oamp3, Parameters.at("oamp3"),
          \oamp4, Parameters.at("oamp4"),
          \oamp5, Parameters.at("oamp5"),
          \oamp6, Parameters.at("oamp6"),
          \oatk1, Parameters.at("oatk1"),
          \oatk2, Parameters.at("oatk2"),
          \oatk3, Parameters.at("oatk3"),
          \oatk4, Parameters.at("oatk4"),
          \oatk5, Parameters.at("oatk5"),
          \oatk6, Parameters.at("oatk6"),
          \fatk, Parameters.at("fatk"),
          \patk, Parameters.at("patk"),
          \odec1, Parameters.at("odec1"),
          \odec2, Parameters.at("odec2"),
          \odec3, Parameters.at("odec3"),
          \odec4, Parameters.at("odec4"),
          \odec5, Parameters.at("odec5"),
          \odec6, Parameters.at("odec6"),
          \fdec, Parameters.at("fdec"),
          \pdec, Parameters.at("pdec"),
          \osus1, Parameters.at("osus1"),
          \osus2, Parameters.at("osus2"),
          \osus3, Parameters.at("osus3"),
          \osus4, Parameters.at("osus4"),
          \osus5, Parameters.at("osus5"),
          \osus6, Parameters.at("osus6"),
          \fsus, Parameters.at("fsus"),
          \psus, Parameters.at("psus"),
          \orel1, Parameters.at("orel1"),
          \orel2, Parameters.at("orel2"),
          \orel3, Parameters.at("orel3"),
          \orel4, Parameters.at("orel4"),
          \orel5, Parameters.at("orel5"),
          \orel6, Parameters.at("orel6"),
          \frel, Parameters.at("frel"),
          \prel, Parameters.at("prel"),
          \oamt1, Parameters.at("omat1"),
          \oamt2, Parameters.at("omat2"),
          \oamt3, Parameters.at("omat3"),
          \oamt4, Parameters.at("omat4"),
          \oamt5, Parameters.at("omat5"),
          \oamt6, Parameters.at("omat6"),
          \hfamt, Parameters.at("hfamt"),
          \lfamt, Parameters.at("lfamt"),
          \pamt, Parameters.at("pamt"),
          \ocurve, Parameters.at("ocurve"),
          \fcurve, Parameters.at("fcurve"),
          \pcurve, Parameters.at("pcurve"),
          \lfreq, Parameters.at("lfreq"),
          \lfade, Parameters.at("lfade"),
          \lfo_am, Parameters.at("lfo_am"),
          \lfo_pm, Parameters.at("lfo_pm"),
          \lfo_hfm, Parameters.at("lfo_hfm"),
          \lfo_lfm, Parameters.at("lfo_lfm"),
          \feedback, Parameters.at("feedback")
          ]);
      );
      NodeWatcher.register(Voices.at(note));
      fnNoteAdd.(note);
    };

    fnNoteAdd = {
      arg note;
      var oldestNote = 0;
      var oldestNoteVal = 10000000;
      PolyphonyCount = PolyphonyCount + 1;
      VoicesOn.put(note, PolyphonyCount);
      if (VoicesOn.size > PolyphonyMax, {
        VoicesOn.keysValuesDo({ arg key, val;
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
      if (Parameters.at("monophonic") > 0, {
        fnNoteOnMono.(note, amp, duration);
      },{
        fnNoteOnPoly.(note, amp, duration);
      });
    };

    fnNoteOff = {
      arg note;
      if ((Voices.at(note)==nil) || ((Voices.at(note).isRunning==false) && (VoicesOn.at(note)==nil)),{},{
        if (Parameters.at("monophonic") > 0, {
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
      VoicesOn.removeAt(note);
      VoicesOn.keysValuesDo({ arg note, syn;
        notesOn=true;
      });
      if (notesOn==false,{
        Voices.keysValuesDo({ arg note, syn;
          if (syn.isRunning, {
            syn.set(\gate, 0);
          });
        });
      },{
        Voices.keysValuesDo({ arg note, syn;
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
      VoicesOn.removeAt(note);

      if (pedalSustainOn==true,{
        pedalSustainNotes.put(note,1);
      },{
        if ((pedalSostenutoOn==true) && (pedalSostenutoNotes.at(note) != nil),{},{
          Voices.at(note).set(\gate,0);
        });
      });
    };

    this.addCommand("note_on", "iff", { arg msg;
      var note = msg[1];
      if (Voices.at(note)!=nil,{
        if (Voices.at(note).isRunning==true,{
          Voices.at(note).set(\gate,0);
        });
      });
    fnNoteOn.msg([1], msg[2], msg[3]);
    });

    this.addCommand("note_off", "i", { arg msg;
      var note = msg[1];
      fnNoteOff.(note);
    });

    this.addCommand("sustain", "i", { arg msg;
      pedalSustainOn=(msg[1]==1);
      if(pedalSustainOn==false, {
        pedalSustainNotes.keysValuesDo({ arg note, val;
          if (VoicesOn.at(note)==nil, {
            pedalSustainNotes.removeAt(note);
            fnNoteOff.(note);
          });
        });
      },{
        VoicesOn.keysValuesDo({ arg note, val;
          pedalSustainNotes.put(note,1);
        });
      });
    });

    this.addCommand("sostenuto", "i", { arg msg;
      pedalSostenutoOn = (msg[1]==1);
      if (pedalSostenutoOn==false, {
        pedalSostenutoNotes.keysValuesDo({ arg note, val;
          if (VoicesOn.at(note)==nil, {
            pedalSostenutoNotes.removeAt(note);
            fnNoteOff.(note);
          });
        });
      },{
        VoicesOn.keysValuesDo({ arg note, val;
          pedalSostenutoNotes.put(note,1);
        });
      });
    });

    this.addCommand("set_polyphony", "i", { arg msg;
      if (msg[1] == 1, {
        Parameters.put("monophonic",1);
      },{
        Parameters.put("monophonic",0);
        PolyphonyMax=msg[1];
      });
    });

    this.addCommand("set", "sf", { arg msg;
      var key = msg[1].asString;
      var val = msg[2];
      Parameters.put(key,val);
      Voices.keysValuesDo({ arg note, syn;
        if (syn.isRunning==true,{
          syn.set(key.asSymbol,val);
        });
      });
    });
  }

  free {
    voices.keysValuesDo({ arg key, value; value.free; });
  }
}
