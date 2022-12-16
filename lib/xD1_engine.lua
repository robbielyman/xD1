Engine_xD1 = {}

local controlspec = require "controlspec"

local function add_params(id)
  local suffix = ""
  if id then
    local NUM_PARAMS = 78
    params:add_group("timbre_" .. id, "Timbre " .. id, NUM_PARAMS)
    suffix = "_" .. id
  end
  id = id or 0
  params:add{
    type    = "control",
    id      = "xd1_amp" .. suffix,
    name    = "amp",
    controlspec = controlspec.new(0, 1, "lin", 0, 0.5),
    action  = function (x)
      engine.set("amp", id, x)
      Engine_xD1.param_changed_callback("xd1_amp" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "monophonic" .. suffix,
    name    = "monophonic",
    controlspec = controlspec.new(0, 1, "lin", 1, 0),
    action  = function (x)
      engine.set("monophonic", id, x)
      Engine_xD1.param_changed_callback("monophonic" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "alg" .. suffix,
    name    = "algorithm",
    controlspec = controlspec.new(0, 31, "lin", 1, 0),
    action  = function (x)
      engine.set("alg", id, x)
      Engine_xD1.param_changed_callback("alg" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "feedback" .. suffix,
    name    = "feedback",
    controlspec = controlspec.new(0, 1.5, "lin", 0, 0),
    action  = function (x)
      engine.set("feedback", id, x)
      Engine_xD1.param_changed_callback("feedback" .. suffix)
    end
  }
  -- lfo
  params:add{
    type    = "control",
    id      = "lfreq" .. suffix,
    name    = "lfo freq",
    controlspec = controlspec.new(0.01, 10, "exp", 0, 1),
    action  = function (x)
      engine.set("lfreq", id, x)
      Engine_xD1.param_changed_callback("lfreq" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "lfade" .. suffix,
    name    = "lfo fade",
    controlspec = controlspec.new(0.01, 10, "exp", 0, 0.01),
    action  = function (x)
      engine.set("lfade", id, x)
      Engine_xD1.param_changed_callback("lfade" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "lfo_am" .. suffix,
    name    = "lfo > amp",
    controlspec = controlspec.new(0, 1, "lin", 0, 0),
    action  = function (x)
      engine.set("lfo_am", id, x)
      Engine_xD1.param_changed_callback("lfo_am" .. suffix)
    end
  }
  params:add_separator("pitch" .. suffix, "pitch")
  params:add{
    type    = "control",
    id      = "patk" .. suffix,
    name    = "attack",
    controlspec = controlspec.new(0.01, 10, "lin", 0, 0.1),
    action  = function (x)
      engine.set("patk", id, x)
      Engine_xD1.param_changed_callback("patk" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "pdec" .. suffix,
    name    = "decay",
    controlspec = controlspec.new(0, 2, "lin", 0, 0.3),
    action  = function (x)
      engine.set("pdec", id, x)
      Engine_xD1.param_changed_callback("pdec" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "psus" .. suffix,
    name    = "sustain",
    controlspec = controlspec.new(0, 1, "lin", 0, 0.7),
    action  = function (x)
      engine.set("psus", id, x)
      Engine_xD1.param_changed_callback("psus" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "prel" .. suffix,
    name    = "release",
    controlspec = controlspec.new(0.01, 10, "lin", 0, 0.2),
    action  = function (x)
      engine.set("prel", id, x)
      Engine_xD1.param_changed_callback("prel" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "pcurve" .. suffix,
    name    = "curve",
    controlspec = controlspec.new(-4, 4, "lin", 0, -1),
    action  = function (x)
      engine.set("pcurve", id, x)
      Engine_xD1.param_changed_callback("pcurve" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "pamt" .. suffix,
    name    = "env > pitch",
    controlspec = controlspec.new(0, 1, "lin", 0, 0),
    action  = function (x)
      engine.set("pamt", id, x)
      Engine_xD1.param_changed_callback("pamt" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "lfo_pm" .. suffix,
    name    = "lfo > pitch",
    controlspec = controlspec.new(0, 1, "lin", 0, 0),
    action  = function (x)
      engine.set("lfo_pm", id, x)
      Engine_xD1.param_changed_callback("lfo_pm" .. suffix)
    end
  }
  -- filters
  params:add_separator("filters" .. suffix, "filters")
  params:add{
    type    = "control",
    id      = "hirat" .. suffix,
    name    = "highpass",
    controlspec = controlspec.new(0.125, 8, "exp", 0, 0.125),
    action  = function (x)
      engine.set("hirat", id, x)
      Engine_xD1.param_changed_callback("hirat" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "hires" .. suffix,
    name    = "res",
    controlspec = controlspec.new(0, 1, "lin", 0, 0),
    action  = function (x)
      engine.set("hires", id, x)
      Engine_xD1.param_changed_callback("hires" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "lorat" .. suffix,
    name    = "lowpass",
    controlspec = controlspec.new(0.125, 8, "exp", 0, 8.0),
    action  = function (x)
      engine.set("lorat", id, x)
      Engine_xD1.param_changed_callback("lorat" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "lores" .. suffix,
    name    = "res",
    controlspec = controlspec.new(0, 1, "lin", 0, 0),
    action  = function (x)
      engine.set("lores", id, x)
      Engine_xD1.param_changed_callback("lores" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "fatk" .. suffix,
    name    = "attack",
    controlspec = controlspec.new(0.01, 10, "lin", 0, 0.1),
    action  = function (x)
      engine.set("fatk", id, x)
      Engine_xD1.param_changed_callback("fatk" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "fdec" .. suffix,
    name    = "decay",
    controlspec = controlspec.new(0, 2, "lin", 0, 0.3),
    action  = function (x)
      engine.set("fdec", id, x)
      Engine_xD1.param_changed_callback("fdec" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "fsus" .. suffix,
    name    = "sustain",
    controlspec = controlspec.new(0, 1, "lin", 0, 0.7),
    action  = function (x)
      engine.set("fsus", id, x)
      Engine_xD1.param_changed_callback("fsus" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "frel" .. suffix,
    name    = "release",
    controlspec = controlspec.new(0.01, 10, "lin", 0, 0.2),
    action  = function (x)
      engine.set("frel", id, x)
      Engine_xD1.param_changed_callback("frel" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "fcurve" .. suffix,
    name    = "curve",
    controlspec = controlspec.new(-4, 4, "lin", 0, -1),
    action  = function (x)
      engine.set("fcurve", id, x)
      Engine_xD1.param_changed_callback("fcurve" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "hfamt" .. suffix,
    name    = "env > highpass",
    controlspec = controlspec.new(0, 1, "lin", 0, 0),
    action  = function (x)
      engine.set("hfamt", id, x)
      Engine_xD1.param_changed_callback("hfamt" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "lfamt" .. suffix,
    name    = "env > lowpass",
    controlspec = controlspec.new(0, 1, "lin", 0, 1),
    action  = function (x)
      engine.set("lfamt", id, x)
      Engine_xD1.param_changed_callback("lfamt" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "lfo_hfm" .. suffix,
    name    = "lfo > highpass",
    controlspec = controlspec.new(0, 1, "lin", 0, 0),
    action  = function (x)
      engine.set("lfo_hfm", id, x)
      Engine_xD1.param_changed_callback("lfo_hfm" .. suffix)
    end
  }
  params:add{
    type    = "control",
    id      = "lfo_lfm" .. suffix,
    name    = "lfo > lowpass",
    controlspec = controlspec.new(0, 1, "lin", 0, 0),
    action  = function (x)
      engine.set("lfo_lfm", id, x)
      Engine_xD1.param_changed_callback("lfo_lfm" .. suffix)
    end
  }
  -- operators
  params:add{
    type    = "control",
    id      = "ocurve" .. suffix,
    name    = "op env curve",
    controlspec = controlspec.new(-4, 4, "lin", 0, -1),
    action  = function (x)
      engine.set("ocurve", id, x)
      Engine_xD1.param_changed_callback("ocurve" .. suffix)
    end
  }
  for i = 1, 6 do
    params:add_separator("operator_" .. i .. suffix, "operator " .. i)
    params:add{
      type    = "control",
      id      = "num_" .. i .. suffix,
      name    = "numerator " .. i,
      controlspec = controlspec.new(1, 30, "lin", 1, 1),
      action  = function (x)
        engine.index_set("num", i, id, x)
        Engine_xD1.param_changed_callback("num" .. i .. suffix)
      end
    }
    params:add{
      type    = "control",
      id      = "denom_" .. i .. suffix,
      name    = "denominator " .. i,
      controlspec = controlspec.new(1, 30, "lin", 1, 1),
      action  = function (x)
        engine.index_set("denom", i, id, x)
        Engine_xD1.param_changed_callback("denom" .. i .. suffix)
      end
    }
    params:add{
      type    = "control",
      id      = "oamp_" .. i .. suffix,
      name    = "index " .. i,
      controlspec = controlspec.new(0, 4, "lin", 0, 1),
      action  = function (x)
        engine.index_set("oamp", i, id, x)
        Engine_xD1.param_changed_callback("oamp" .. i .. suffix)
      end
    }
    params:add{
      type    = "control",
      id      = "oatk_" .. i .. suffix,
      name    = "attack " .. i,
      controlspec = controlspec.new(0.01, 10, "lin", 0, 0.1),
      action  = function (x)
        engine.index_set("oatk", i, id, x)
        Engine_xD1.param_changed_callback("oatk" .. i .. suffix)
      end
    }
    params:add{
      type    = "control",
      id      = "odec_" .. i .. suffix,
      name    = "decay " .. i,
      controlspec = controlspec.new(0, 2, "lin", 0, 0.3),
      action  = function (x)
        engine.index_set("odec", i, id, x)
        Engine_xD1.param_changed_callback("odec" .. i .. suffix)
      end
    }
    params:add{
      type    = "control",
      id      = "osus_" .. i .. suffix,
      name    = "sustain " .. i,
      controlspec = controlspec.new(0, 1, "lin", 0, 0.7),
      action  = function (x)
        engine.index_set("osus", i, id, x)
        Engine_xD1.param_changed_callback("osus" .. i .. suffix)
      end
    }
    params:add{
      type    = "control",
      id      = "orel_" .. i .. suffix,
      name    = "release " .. i,
      controlspec = controlspec.new(0.01, 10, "lin", 0, 0.2),
      action  = function (x)
        engine.index_set("orel", i, id, x)
        Engine_xD1.param_changed_callback("orel" .. i .. suffix)
      end
    }
  end
end

local function add_midi_event(mididevices, mididevice_list, midi_channels, dev)
  if not dev.port then return end
  local name = string.lower(dev.name)
  table.insert(mididevice_list, name)
  print("adding " .. name .. " to port " .. dev.port)
  mididevices[name] = {
    name = name,
    port = dev.port,
    midi = midi.connect(dev.port),
    active = false,
  }
  mididevices[name].midi.event = function (data)
    if mididevices[name].active == false then return end
    local d = midi.to_msg(data)
    if d.ch ~= midi_channels[params:get("midichannel")] and params:get("midichannel") > 1 then
      return
    end
    if d.type == "note_on" then
      local amp = util.linexp(1, 127, 0.01, 1.2, d.vel)
      engine.note_on(d.note, amp, 0)
      Engine_xD1.note_on_callback(d.note, amp, 0)
    elseif d.type == "note_off" then
      engine.note_off(d.note, 0)
      Engine_xD1.note_off_callback(d.note, 0)
    elseif d.cc == 64 then
      local val = d.val > 126 and 1 or 0
      if params:get("pedal_mode") == 1 then
        engine.sustain(val)
      else
        engine.sostenuto(val)
      end
    end
  end
end

local function add_midi_params()
  params:add_separator("midi_sep", "midi")
  local mididevices = {}
  local mididevice_list = {"none"}
  local midi_channels = {"all"}
  for i = 1, 16 do
    table.insert(midi_channels, i)
  end
  for _, dev in pairs(midi.devices) do
    add_midi_event(mididevices, mididevice_list, midi_channels, dev)
  end
  tab.print(mididevice_list)
  params:add{
    type    = "option",
    id      = "pedal_mode",
    name    = "pedal mode",
    options = {"sustain", "sostenuto"},
    default = 1,
  }
  params:add{
    type    = "option",
    id      = "midi",
    name    = "midi in",
    options = mididevice_list,
    default = 1,
    action  = function (x)
      if x == 1 then return end
      for _, dev in pairs(mididevices) do
        dev.active = false
      end
      mididevices[mididevice_list[x]].active = true
    end
  }
  params:add{
    type    = "option",
    id      = "midichannel",
    name    = "midi ch",
    options = midi_channels,
    default = 1
  }

  if #mididevice_list > 1 then
    params:set("midi", 2)
  end
end

function Engine_xD1.init(add_midi, timbrality)
  timbrality = timbrality or 1
  params:add{
    type    = "control",
    id      = "max_polyphony",
    name    = "max polyphony",
    controlspec = controlspec.new(1, 20, "lin", 1, 20),
    action  = function (x)
      engine.set_polyphony(x)
      Engine_xD1.param_changed_callback("max_polyphony")
    end
  }
  if add_midi then
    add_midi_params()
  end
  if timbrality == 1 then
    add_params()
    return
  end
  for i = 0, timbrality - 1 do
    add_params(i)
  end
end

function Engine_xD1.note_on(note, vel, timbre)
  if not timbre then timbre = 0 end
  engine.note_on(note, vel, timbre)
end

function Engine_xD1.note_off(note, timbre)
  if not timbre then timbre = 0 end
  engine.note_off(note, timbre)
end

function Engine_xD1.param_changed_callback(_) end
function Engine_xD1.note_on_callback(...) end
function Engine_xD1.note_off_callback(...) end

return Engine_xD1
