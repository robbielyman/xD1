Engine_xD1 = {}

local controlspec = require "controlspec"

--- adds params
-- @tparam bool bool build midi
function Engine_xD1:params(bool)
    -- misc
    params:add{
        type    = "control",
        id      = "xd1_amp",
        name    = "amp",
        controlspec = controlspec.new(0, 1, "lin", 0, 0.5, ""),
        action  = function(x)
            engine.set("amp", x)
            self.param_changed_callback("xd1_amp")
        end
    }
    params:add{
        type    = "control",
        id      = "alg",
        name    = "algorithm",
        controlspec = controlspec.new(0, 31, "lin", 1, 0),
        action  = function(x)
            engine.set("alg", x)
            self.param_changed_callback("alg")
        end
    }
    params:add{
        type    = "control",
        id      = "max_polyphony",
        name    = "max polyphony",
        controlspec = controlspec.new(1, 20, "lin", 1, 20),
        action  = function(x)
            engine.set_polyphony(x)
            self.param_changed_callback("max_polyphony")
        end
    }
    params:add{
        type    = "control",
        id      = "feedback",
        name    = "feedback",
        controlspec = controlspec.new(0, 1.5, "lin", 0, 0, ""),
        action  = function(x)
            engine.set("feedback", x)
            self.param_changed_callback("feedback")
        end
    }
    -- lfo
    params:add{
        type    = "control",
        id      = "lfreq",
        name    = "lfo freq",
        controlspec = controlspec.new(0.01, 10, "exp", 0, 1),
        action  = function(x)
            engine.set("lfreq", x)
            self.param_changed_callback("lfreq")
        end
    }
    params:add{
        type    = "control",
        id      = "lfade",
        name    = "lfo fade",
        controlspec = controlspec.new(0.01, 10, "exp", 0, 0.01),
        action  = function(x)
            engine.set("lfade", x)
            self.param_changed_callback("lfade")
        end
    }
    params:add{
        type    = "control",
        id      = "lfo_am",
        name    = "lfo > amp",
        controlspec = controlspec.new(0, 1, "lin", 0, 0),
        action  = function(x)
            engine.set("lfo_am", x)
            self.param_changed_callback("lfo_am")
        end
    }
    -- pitch
    params:add_group("pitch", "pitch", 7)
    params:add{
        type    = "control",
        id      = "patk",
        name    = "attack",
        controlspec = controlspec.new(0.01, 10, "lin", 0, 0.1),
        action  = function(x)
            engine.set("patk", x)
            self.param_changed_callback("patk")
        end
    }
    params:add{
        type    = "control",
        id      = "pdec",
        name    = "decay",
        controlspec = controlspec.new(0, 2, "lin", 0, 0.3),
        action  = function(x)
            engine.set("pdec", x)
            self.param_changed_callback("pdec")
        end
    }
    params:add{
        type    = "control",
        id      = "psus",
        name    = "sustain",
        controlspec = controlspec.new(0, 1, "lin", 0, 0.7),
        action = function(x)
            engine.set("psus", x)
            self.param_changed_callback("psus")
        end
    }
    params:add{
        type    = "control",
        id      = "prel",
        name    = "release",
        controlspec = controlspec.new(0.01, 10, "lin", 0, 0.2),
        action  = function(x)
            engine.set("prel", x)
            self.param_changed_callback("prel")
        end
    }
    params:add{
        type    = "control",
        id      = "pcurve",
        name    = "curve",
        controlspec = controlspec.new(-4, 4, "lin", 0, -1),
        action  = function(x)
            engine.set("pcurve", x)
            self.param_changed_callback("pcurve")
        end
    }
    params:add{
        type    = "control",
        id      = "pamt",
        name    = "env > pitch",
        controlspec = controlspec.new(0, 1, "lin", 0, 0),
        action  = function(x)
            engine.set("pamt", x)
            self.param_changed_callback("pamt")
        end
    }
    params:add{
        type    = "control",
        id      = "lfo_pm",
        name    = "lfo > pitch",
        controlspec = controlspec.new(0, 1, "lin", 0, 0),
        action  = function(x)
            engine.set("lfo_pm", x)
            self.param_changed_callback("lfo_pm")
        end
    }
    -- highpass
    params:add_group("filters", "filters", 13)
    params:add{
        type    = "control",
        id      = "hirat",
        name    = "highpass",
        controlspec = controlspec.new(0.125, 8, "exp", 0, 0.125),
        action  = function(x)
            engine.set("hirat", x)
            self.param_changed_callback("hirat")
        end
    }
    params:add{
        type    = "control",
        id      = "hires",
        name    = "res",
        controlspec = controlspec.new(0, 1, "lin", 0, 0),
        action  = function(x)
            engine.set("hires", x)
            self.param_changed_callback("hires")
        end
    }
    params:add{
        type    = "control",
        id      = "lorat",
        name    = "lowpass",
        controlspec = controlspec.new(0.125, 8, "exp", 0, 8.0),
        action  = function(x)
            engine.set("lorat", x)
            self.param_changed_callback("lorat")
        end
    }
    params:add{
        type    = "control",
        id      = "lores",
        name    = "res",
        controlspec = controlspec.new(0, 1, "lin", 0, 0),
        action  = function(x)
            engine.set("lores", x)
            self.param_changed_callback("lores")
        end
    }
    params:add{
        type    = "control",
        id      = "fatk",
        name    = "attack",
        controlspec = controlspec.new(0.01, 10, "lin", 0, 0.1),
        action  = function(x)
            engine.set("fatk", x)
            self.param_changed_callback("fatk")
        end
    }
    params:add{
        type    = "control",
        id      = "fdec",
        name    = "decay",
        controlspec = controlspec.new(0, 2, "lin", 0, 0.3),
        action  = function(x)
            engine.set("fdec", x)
            self.param_changed_callback("fdec")
        end
    }
    params:add{
        type    = "control",
        id      = "fsus",
        name    = "sustain",
        controlspec = controlspec.new(0, 1, "lin", 0, 0.7),
        action  = function(x)
            engine.set("fsus", x)
            self.param_changed_callback("fsus")
        end
    }
    params:add{
        type    = "control",
        id      = "frel",
        name    = "release",
        controlspec = controlspec.new(0.01, 10, "lin", 0, 0.2),
        action  = function(x)
            engine.set("frel", x)
            self.param_changed_callback("frel")
        end
    }
    params:add{
        type    = "control",
        id      = "fcurve",
        name    = "curve",
        controlspec = controlspec.new(-4, 4, "lin", 0, -1),
        action  = function(x)
            engine.set("fcurve", x)
            self.param_changed_callback("fcurve")
        end
    }
    params:add{
        type    = "control",
        id      = "hfamt",
        name    = "env > highpass",
        controlspec = controlspec.new(0, 1, "lin", 0, 0),
        action  = function(x)
            engine.set("hfamt", x)
            self.param_changed_callback("hfamt")
        end
    }
    params:add{
        type    = "control",
        id      = "lfamt",
        name    = "env > lowpass",
        controlspec = controlspec.new(0, 1, "lin", 0, 1),
        action  = function(x)
            engine.set("lfamt", x)
            self.param_changed_callback("lfamt")
        end
    }
    params:add{
        type    = "control",
        id      = "lfo_hfm",
        name    = "lfo > highpass",
        controlspec = controlspec.new(0, 1, "lin", 0, 0),
        action  = function(x)
            engine.set("lfo_hfm", x)
            self.param_changed_callback("lfo_hfm")
        end
    }
    params:add{
        type    = "control",
        id      = "lfo_lfm",
        name    = "lfo > lowpass",
        controlspec = controlspec.new(0, 1, "lin", 0, 0),
        action = function(x)
            engine.set("lfo_lfm", x)
            self.param_changed_callback("lfo_lfm")
        end
    }
    -- operators
    params:add{
        type    = "control",
        id      = "ocurve",
        name    = "op env curve",
        controlspec = controlspec.new(-4, 4, "lin", 0, -1),
        action  = function(x)
            engine.set("ocurve", x)
            self.param_changed_callback("ocurve")
        end
    }
    for i = 1, 6 do
        params:add_group("operator_"..i, "operator " .. i, 7)
        params:add{
            type    = "control",
            id      = "num" .. i,
            name    = "numerator ",
            controlspec = controlspec.new(1, 30, "lin", 1, 1),
            action  = function(x)
                engine.set("num" .. i, x)
                self.param_changed_callback("num" .. i)
            end
        }
        params:add{
            type    = "control",
            id      = "denom" .. i,
            name    = "denominator ",
            controlspec = controlspec.new(1, 30, "lin", 1, 1),
            action  = function(x)
                engine.set("denom" .. i, x)
                self.param_changed_callback("denom" .. i)
            end
        }
        params:add{
            type    = "control",
            id      = "oamp"..i,
            name    = "index",
            controlspec = controlspec.new(0, 4, "lin", 0, 1),
            action  = function(x)
                engine.set("oamp"..i, x)
                self.param_changed_callback("oamp"..i)
            end
        }
        params:add{
            type    = "control",
            id      = "oatk"..i,
            name    = "attack",
            controlspec = controlspec.new(0.01, 10, "lin", 0, 0.1),
            action  = function(x)
                engine.set("oatk"..i, x)
                self.param_changed_callback("oatk"..i)
            end
        }
        params:add{
            type    = "control",
            id      = "odec"..i,
            name    = "decay",
            controlspec = controlspec.new(0, 2, "lin", 0, 0.3),
            action  = function(x)
                engine.set("odec"..i, x)
                self.param_changed_callback("odec"..i)
            end
        }
        params:add{
            type    = "control",
            id      = "osus"..i,
            name    = "sustain",
            controlspec = controlspec.new(0, 1, "lin", 0, 0.7),
            action  = function(x)
                engine.set("osus"..i, x)
                self.param_changed_callback("osus"..i)
            end
        }
        params:add{
            type    = "control",
            id      = "orel" ..i,
            name    = "release",
            controlspec = controlspec.new(0.01, 10, "lin", 0, 0.2),
            action  = function(x)
                engine.set("orel"..i, x)
                self.param_changed_callback("orel"..i)
            end
        }
    end
    if not bool then return end
    params:add_separator("midi_sep", "midi")
    local mididevice = {}
    local mididevice_list = {"none"}
    local midi_channels = {"all"}
    for i = 1, 16 do
        table.insert(midi_channels, i)
    end
    for _,dev in pairs(midi.devices) do
        if dev.port ~= nil then
            local name = string.lower(dev.name)
            table.insert(mididevice_list,name)
            print("adding " .. name .. " to port " .. dev.port)
            mididevice[name] = {
                name = name,
                port = dev.port,
                midi = midi.connect(dev.port),
                active = false,
            }
            mididevice[name].midi.event = function(data)
                if mididevice[name].active == false then
                    return
                end
                local d = midi.to_msg(data)
                if d.ch ~= midi_channels[params:get("midichannel")]
                    and params:get("midichannel") > 1 then
                    return
                end
                if d.type == "note_on" then
                    local amp = util.linexp(1, 127, 0.01, 1.2, d.vel)
                    engine.note_on(d.note, amp, 600)
                    self.note_on_callback(d.note, amp, 600)
                elseif d.type == "note_off" then
                    engine.note_off(d.note)
                    self.note_off_callback(d.note)
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
        action  = function(x)
            if x == 1 then return end
            for _,dev in pairs(mididevice) do
                dev.active = false
            end
            mididevice[mididevice_list[x]].active = true
        end
    }
    params:add{
        type    = "option",
        id      = "midichannel",
        name    = "midi ch",
        options = midi_channels,
        default = 1
    }

    if #mididevice_list>1 then
        params:set("midi",2)
    end
end

function Engine_xD1.killall()
    engine.killall()
end

function Engine_xD1.param_changed_callback(_) end

function Engine_xD1.note_on(note, vel, time)
    if not time then time = 600 end
    engine.note_on(note, vel, time)
end

function Engine_xD1.note_off(note)
    engine.note_off(note)
end

function Engine_xD1.note_on_callback(...) end
function Engine_xD1.note_off_callback(...) end

return Engine_xD1
