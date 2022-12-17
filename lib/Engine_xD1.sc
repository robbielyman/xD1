// polytimbral FM polysynth engine
// Polyphony code inspired by MxSynths by infinitedigits @schollz

// this is a CroneEngine
Engine_xD1 : CroneEngine {
  var endOfChain,
  outBus,
  xParameters,
  xVoices,
  xVoicesOn,
  xTimbres,
  fnNoteOn, fnNoteOnMono, fnNoteOnPoly, fnNoteAdd,
  fnNoteOff, fnNoteOffMono, fnNoteOffPoly,
  pedalSustainOn=false,
  pedalSostenutoOn=false,
  pedalSustainNotes, pedalSostenutoNotes,
  timbralityMax=256, polyphonyMax=20, polyphonyCount=0;

  *new {
    arg context, doneCallback;
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

    xParameters = (
      amp: 0.5,
      monophonic: 0,
      alg: 0,

      num: (1 ! 6),
      denom: (1 ! 6),

      hirat: 0.125,
      lorat: 8,
      hires: 0,
      lores: 0,
      
      oamp: (1 ! 6),

      oatk: (0.1 ! 6),
      fatk: 0.1,
      patk: 0.1,

      odec: (0.3 ! 6),
      fdec: 0.3,
      pdec: 0.3,

      osus: (0.7 ! 6),
      fsus: 0.7,
      psus: 0.7,

      orel: (0.2 ! 6),
      frel: 0.2,
      prel: 0.2,

      hfamt: 0,
      lfamt: 1,
      pamt: 0,

      ocurve: -1,
      fcurve: -1,
      pcurve: 0,

      lfreq: 1,
      lfade: 0,
      lfo_am: 0,
      lfo_pm: 0,
      lfo_hfm: 0,
      lfo_lfm: 0,

      feedback: 0,
    );

    xTimbres = Array.fill(timbralityMax, { xParameters.deepCopy; });
    xVoices = Dictionary.new;
    xVoicesOn = Dictionary.new;
    pedalSustainNotes = Dictionary.new;
    pedalSostenutoNotes = Dictionary.new;

    32.do({ arg i; SynthDef(("xD1_"++i).asString, {
      var gate = \gate.kr(1);
      var num = \num.kr(1 ! 6);
      var denom = \denom.kr(1 ! 6);
      var oatk = \oatk.kr(0.1 ! 6);
      var odec = \odec.kr(0.3 ! 6);
      var osus = \osus.kr(0.7 ! 6);
      var orel = \orel.kr(0.2 ! 6);
      var maxrel = ArrayMax.kr(orel)[0];
      var menv = Env.asr(0, 1, maxrel).kr(2, gate);
      var fenv = Env.adsr(\fatk.kr(0.1), \fdec.kr(0.3), \fsus.kr(0.7), \frel.kr(0.2), 1, \fcurve.kr(-1)).kr(0, gate);
      var penv = Env.adsr(\patk.kr(0.1), \pdec.kr(0.3), \psus.kr(0.7), \prel.kr(0.2), \pamt.kr(0), \pcurve.kr(0)).kr(0, gate);
      var oenv = Env.adsr(oatk, odec, osus, orel, 1, \ocurve.kr(-1)).kr(0, gate);
      var oamp = \oamp.kr(1 ! 6);
      var ratios = Array.fill(6, { arg i; num[i] / denom[i]; });
      var lfo = LFTri.kr(\lfreq.kr(1), mul:Env.asr(\lfade.kr(0), 1, 10).kr(0, gate));
      var alfo = lfo.madd(0.05, 1.0) * \lfo_am.kr(0);
      var note = \note.kr(69);
      var pitch = (note + (1.2 * \lfo_pm.kr(0) * lfo) + (1.2 * penv)).midicps;
      var ctls = Array.fill(6, { arg i;
        [pitch * ratios[i], 0, (oenv[i] + alfo) * oamp[i]];
      });
      var hifreq = \hirat.kr(0.125) * (note + (1.2 * \lfo_hfm.kr(0) * lfo) + (1.2 * fenv * \hfamt.kr(0))).midicps;
      var lofreq = \lorat.kr(8) * (note + (1.2 * \lfo_lfm.kr(0) * lfo) + (1.2 * fenv * \lfamt.kr(1))).midicps;
      var snd = Mix.ar(FM7.arAlgo(i, ctls, \feedback.kr(0)));
      snd = SVF.ar(snd, hifreq, \hires.kr(0), lowpass:0, highpass:1);
      snd = SVF.ar(snd, lofreq, \lores.kr(0));
      Out.ar(\out.ir, (snd * \amp.kr(0.5) * menv * 0.5));
    }).add; });

    fnNoteOnMono = {
      arg note, amp, timbre;
      var notesOn = false;
      var setNote = false;
      xVoices.keysValuesDo({ arg key, syn;
        if ((key.timbre==timbre) && (syn.isPlaying == true), {
          notesOn = true;
        });
      });
      if (notesOn==false, {
        fnNoteOnPoly.(note, amp, timbre);
      }, {
          xVoices.keysValuesDo({ arg key, syn;
            if ((key.timbre==timbre) && (syn.isPlaying == true), {
              syn.set(\gate, 0);
              if (setNote==false, {
                syn.set(\gate, 1, \note, note);
                setNote = true;
              });
            });
          });
      });
      fnNoteAdd.(note, timbre);
    };

    fnNoteOnPoly = {
      arg note, amp, timbre;
      var key = (note: note, timbre: timbre);
      var def = ("xD1_" ++ (xTimbres[timbre].alg).asInteger).asString;

      xVoices.put((note: note, timbre: timbre),
        Synth.before(endOfChain, def, [
          \out,     outBus,
          \note,    note,
          \amp,     amp * xTimbres[timbre].amp,
          \gate,    1,
          \num,     xTimbres[timbre].num,
          \denom,   xTimbres[timbre].denom,
          \hirat,   xTimbres[timbre].hirat,
          \lorat,   xTimbres[timbre].lorat,
          \oamp,    xTimbres[timbre].oamp,
          \oatk,    xTimbres[timbre].oatk,
          \patk,    xTimbres[timbre].patk,
          \fatk,    xTimbres[timbre].fatk,
          \odec,    xTimbres[timbre].odec,
          \pdec,    xTimbres[timbre].pdec,
          \fdec,    xTimbres[timbre].fdec,
          \osus,    xTimbres[timbre].osus,
          \psus,    xTimbres[timbre].psus,
          \fsus,    xTimbres[timbre].fsus,
          \orel,    xTimbres[timbre].orel,
          \prel,    xTimbres[timbre].prel,
          \frel,    xTimbres[timbre].frel,
          \hfamt,   xTimbres[timbre].hfmat,
          \lfamt,   xTimbres[timbre].lfamt,
          \ocurve,  xTimbres[timbre].ocurve,
          \pcurve,  xTimbres[timbre].pcurve,
          \fcurve,  xTimbres[timbre].fcurve,
          \lfreq,   xTimbres[timbre].lfreq,
          \lfade,   xTimbres[timbre].lfade,
          \lfo_am,  xTimbres[timbre].lfo_am,
          \lfo_pm,  xTimbres[timbre].lfo_pm,
          \lfo_hfm, xTimbres[timbre].lfo_hfm,
          \lfo_lfm, xTimbres[timbre].lfo_lfm,
          \feedback,xTimbres[timbre].feedback
        ]);
      );
      NodeWatcher.register(xVoices.at(key), true);
      fnNoteAdd.(note, timbre);
    };

    fnNoteAdd = {
      arg note, timbre;
      var oldestNote = 0;
      var oldestNoteVal = 10000000;
      polyphonyCount = polyphonyCount + 1;
      xVoicesOn.put((note: note, timbre: timbre), polyphonyCount);
      if (xVoicesOn.size > polyphonyMax, {
        xVoicesOn.keysValuesDo({ arg key, val;
          if (val < oldestNoteVal, {
            oldestNoteVal = val;
            oldestNote = key;
          });
        });
      ("max polyphony reached, removing note " ++ oldestNote).asString.postln;
      fnNoteOff.(oldestNote.note, oldestNote.timbre);
      });
    };

    fnNoteOn = {
      arg note, amp, timbre;
      var key = (note: note, timbre: timbre);
      if (xVoices.at(key) != nil, {
        fnNoteOff.(note, timbre);
      });
      if (xTimbres[timbre].monophonic > 0, {
        fnNoteOnMono.(note, amp, timbre);
      }, {
          fnNoteOnPoly.(note, amp, timbre);
      });
    };

    fnNoteOff = {
      arg note, timbre;
      if (xTimbres[timbre].monophonic > 0, {
        fnNoteOffMono.(note, timbre);
      }, {
          fnNoteOffPoly.(note, timbre);
      });
    };

    fnNoteOffMono = {
      arg note, timbre;
      var notesOn = false;
      var playedAnother = false;
      xVoicesOn.removeAt((note: note, timbre: timbre));
      xVoicesOn.keysValuesDo({ arg key, val;
        if (key.timbre==timbre, {
          notesOn = true;
        });
      });
      if (notesOn==false, {
        xVoices.keysValuesDo({ arg key, syn;
          if ((key.timbre==timbre) && (syn.isPlaying == true), {
            syn.release();
            xVoices.removeAt(key);
          });
        });
      }, {
          xVoices.keysValuesDo({ arg key, syn;
            if ((key.timbre==timbre) && (syn.isPlaying == true), {
              syn.release();
              if (playedAnother==false, {
                syn.set(\gate, 1, \note, key.note);
                playedAnother = true;
              });
            });
          });
      });
    };

    fnNoteOffPoly = {
      arg note, timbre;
      var key = (note: note, timbre: timbre);
      xVoicesOn.removeAt(key);

      if (pedalSustainOn==true, {
        pedalSustainNotes.put(key, 1);
      }, {
          if ((pedalSostenutoOn==true) && (pedalSustainNotes.at((note:note, timbre:timbre)) != nil),{},{
            if (xVoices.at(key) != nil, {
              xVoices.at(key).release();
              xVoices.removeAt(key);
            });
          });
        });
    };

    this.addCommand("note_on", "ifi", { arg msg;
      fnNoteOn.(msg[1], msg[2], msg[3]);
    });

    this.addCommand("note_off", "ii", { arg msg;
      fnNoteOff.(msg[1], msg[2]);
    });

    this.addCommand("sustain", "i", { arg msg;
      pedalSustainOn = (msg[1] == 1);
      if (pedalSustainOn==false, {
        pedalSustainNotes.keysValuesDo({ arg key, val;
          if (xVoicesOn.at(key)==nil, {
            pedalSustainNotes.removeAt(key);
            fnNoteOff.(key.note, key.timbre);
          });
        });
      }, {
          xVoicesOn.keysValuesDo({ arg key, val;
            pedalSustainNotes.put(key, 1);
          });
      });
    });

    this.addCommand("sostenuto", "i", {arg msg;
      pedalSostenutoOn = (msg[1] == 1);
      if (pedalSostenutoOn == false, {
        pedalSostenutoNotes.keysValuesDo({ arg key, val;
          if (xVoicesOn.at(key) == nil, {
            pedalSostenutoNotes.removeAt(key);
            fnNoteOff.(key.note, key.timbre);
          });
        });
      },{
          xVoicesOn.keysValuesDo({ arg key, val;
            pedalSostenutoNotes.put(key, 1);
          });
      });
    });

    this.addCommand("set_timbre_monophonic", "ii", { arg msg;
      if (msg[1] == 1, {
        xTimbres[msg[2]].monophonic = 1;
      }, {
          xTimbres[msg[2]].monophonic = 0;
      });
    });

    this.addCommand("set_polyphony", "i", { arg msg;
      polyphonyMax = msg[1];
    });

    this.addCommand("set", "sif", { arg msg;
      var key = msg[1].asSymbol;
      var val = msg[3];
      xTimbres[msg[2]].put(key, val);
      if (msg[1] == "alg", {}, {
        xVoices.keysValuesDo({ arg key, syn;
          if ((key.timbre == msg[2]) && (syn.isPlaying == true), {
            syn.set(key, val);
          });
        });
      });
    });

    this.addCommand("index_set", "siif", { arg msg;
      var key = msg[1].asSymbol;
      var index = msg[2] - 1;
      var curr = xTimbres[msg[3]].at(key);
      curr = Array.fill(6, { arg i;
        if (i == index, {
          msg[4];
        }, {
            curr[i];
        });
      });
      xTimbres[msg[3]].put(key, curr);
      xVoices.keysValuesDo({ arg key, syn;
        if ((key.timbre == msg[3]) && (syn.isPlaying == true), {
          syn.set(key, curr);
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
