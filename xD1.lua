-- xD1
--
-- DX7-inspired FM polysynth 
-- with filters
--
-- @alanza
-- v0.1

local xD1 = include("lib/xD1_engine.lua")
UI = require("ui")
Filtergraph = require("filtergraph")
Envgraph = require("envgraph")
Graph = require("graph")

engine.name = "xD1"

function init()
    xD1.init(true)
    Pages = UI.Pages.new(3, 3)

    -- pages
    local ophook = function(self)
        self.lists[1].index = util.clamp(self.index - 4, 1, 4)
        self.lists[1].active = self.index > 4
        self.lists[2].index = util.clamp(self.index - 4, 1, 4)
        for j = 1,4 do
            self.lists[2].entries[j] = params:get(self.params[j + 4])
        end
        self.lists[2].text_align = "right"
        self.lists[2].active = self.index > 4
        self.env_graph:edit_adsr(params:get(self.params[1]), params:get(self.params[2]),
        params:get(self.params[3]), params:get(self.params[4]))
        self.env_graph:redraw()
        local adsr = {"A", "D", "S", "R"}
        for i = 1,4 do
            if self.index == i then screen.level(15) else screen.level(3) end
            screen.move(4, 27 + 11 * (i - 1))
            screen.text(adsr[i] .. " " .. params:string(self.params[i]))
        end
        screen.fill()
    end
    local titles = {}
    local tabs = {}
    for i = 1, 6 do
        titles[i] = tostring(i)
        tabs[i] = Tab.new(
        { "oatk"..i, "odec"..i, "osus"..i, "orel"..i, "num"..i, "denom"..i, "oamp"..i, "ocurve"},
        {
            UI.List.new(70, 24, 1, {"num", "denom", "index", "curve"}),
            UI.List.new(120, 24)
        },
        ophook
        )
        local env_graph = Envgraph.new_adsr(0, 20, nil, nil,
        params:get("oatk"..i), params:get("odec"..i), params:get("osus"..i), params:get("orel"..i),
        1, params:get("ocurve"))
        env_graph:set_position_and_size(4, 22, 56, 38)
        tabs[i].env_graph = env_graph
    end
    OpPage = Page.new(titles, tabs)
    FiltPage = Page.new({"FILTER", "LFO"},
    {
        Tab.new({"fatk", "fdec", "fsus", "frel", "hirat", "lorat", "hfamt", "lfamt", "fcurve"},
        {
            UI.ScrollingList.new(70, 24, 1, {"high", "low", "env > hi", "env > low", "curve"}),
            UI.ScrollingList.new(120, 24)
        },
        function(self)
            self.lists[1].index = util.clamp(self.index - 4, 1, 5)
            self.lists[1].active = self.index > 4
            self.lists[1].num_above_selected = 0
            self.lists[2].index = util.clamp(self.index - 4, 1, 5)
            self.lists[2].active = self.index > 4
            self.lists[2].num_above_selected = 0
            for i = 1, 5 do
                self.lists[2].entries[i] = params:get(self.params[i + 4])
            end
            self.lists[2].text_align = "right"
            self.env_graph:edit_adsr(params:get(self.params[1]), params:get(self.params[2]),
            params:get(self.params[3]), params:get(self.params[4]))
            self.env_graph:redraw()
            local adsr = {"A", "D", "S", "R"}
            for i = 1,4 do
                if self.index == i then screen.level(15) else screen.level(3) end
                screen.move(4, 27 + 11 * (i - 1))
                screen.text(adsr[i] .. " " .. params:string(self.params[i]))
            end
            screen.fill()
        end),
        Tab.new({"lfreq", "lfade", "lfo_am", "lfo_pm", "lfo_hfm", "lfo_lfm"},
        {
            UI.ScrollingList.new(70, 24, 1, {"freq", "fade", "lfo > amp", "lfo > pitch", "lfo > hi", "lfo > low"}),
            UI.ScrollingList.new(120, 24)
        },
        function(self)
            self.lists[1].index = self.index
            self.lists[1].num_above_selected = 0
            self.lists[2].index = self.index
            self.lists[2].num_above_selected = 0
            for i = 1, 6 do
                self.lists[2].entries[i] = params:get(self.params[i])
            end
            self.lists[2].text_align = "right"
            self.lfo_graph:update_functions()
            self.lfo_graph:redraw()
        end)
    })
    FiltPage.tabs[1].env_graph = Envgraph.new_adsr(0, 20, nil, nil,
    params:get("fatk"), params:get("fdec"), params:get("fsus"), params:get("frel"),
    1, params:get("fcurve"))
    FiltPage.tabs[1].env_graph:set_position_and_size(4, 22, 56, 38)
    FiltPage.tabs[2].lfo_graph = Graph.new(0, 1, "lin", -1, 1, "lin", nil, true, false)
    FiltPage.tabs[2].lfo_graph:set_position_and_size(4, 22, 56, 38)
    FiltPage.tabs[2].lfo_graph:add_function( function(x)
        local freq = params:get("lfreq")
        local fade = params:get("lfade")
        local fade_end
        local y_fade
        local MIN_Y = 0.15

        fade_end = util.linlin(0, 10, 0, 1, fade)
        y_fade = util.linlin(0, fade_end, MIN_Y, 1, x)
        x = x * util.linlin(0.01, 10, 0.5, 10, freq)
        local y = math.sin(x * math.pi * 2)
        return y * y_fade * 0.75
    end, 4)
    MiscPage = Page.new({"MISC", "PITCH ENV"},
    {
        Tab.new({"alg", "max_polyphony", "feedback"},
        {
            UI.List.new(70, 34, 1, {"algorithm", "polyphony", "feedback"}),
            UI.List.new(120, 34)
        },
        function(self)
            self.lists[1].index = self.index
            self.lists[2].index = self.index
            for i = 1, 3 do
                self.lists[2].entries[i] = params:get(self.params[i])
            end
            self.lists[2].text_align = "right"
            screen.level(10)
            screen.display_png(norns.state.path .. "/img/" .. params:get("alg") .. ".png", 4, 24)
            screen.fill()
        end),
        Tab.new({"patk", "pdec", "psus", "prel", "pamt", "pcurve"},
        {
            UI.List.new(70, 34, 1, {"env > pitch", "curve"}),
            UI.List.new(120, 34)
        },
        function(self)
            self.lists[1].index = util.clamp(self.index - 4, 1, 2)
            self.lists[1].active = self.index > 4
            self.lists[2].index = util.clamp(self.index - 4, 1, 2)
            for i = 1, 2 do
                self.lists[2].entries[i] = params:get(self.params[i + 4])
            end
            self.lists[2].active = self.index > 4
            self.lists[2].text_align = "right"
            self.env_graph:edit_adsr(params:get(self.params[1]), params:get(self.params[2]),
            params:get(self.params[3]), params:get(self.params[4]))
            self.env_graph:redraw()
            local adsr = {"A", "D", "S", "R"}
            for i = 1,4 do
                if self.index == i then screen.level(15) else screen.level(3) end
                screen.move(4, 27 + 11 * (i - 1))
                screen.text(adsr[i] .. " " .. params:string(self.params[i]))
            end
            screen.fill()
        end)
    })
    MiscPage.tabs[2].env_graph = Envgraph.new_adsr(0, 20, nil, nil,
    params:get("patk"), params:get("pdec"), params:get("psus"), params:get("prel"),
    1, params:get("pcurve"))
    MiscPage.tabs[2].env_graph:set_position_and_size(4, 22, 56, 38)
    params:bang()
    redraw()
end

function xD1.param_changed_callback(id)
    local page
    if Pages.index == 1 then
        page = OpPage
    elseif Pages.index == 2 then
        page = FiltPage
    elseif Pages.index == 3 then
        page = MiscPage
    end
    local tab = page.tabs[page.ui.index]
    local found = false
    for _,v in pair(tab.params) do
        if v == id then
            found = true
            break
        end
    end
    if not found then
        Popup = {
            text = params:lookup_param(id).name .. ": " .. params:get(id),
            redraw = function(self)
                screen.level(0)
                screen.rect(8, 0, 128 - 16, 6, 0)
                screen.move(64, 6)
                screen.level(8)
                screen.text_center(self.text)
                screen.fill()
            end
        }
        if Popup_Clock then
            clock.cancel(Popup_Clock)
        end
        Popup_Clock = clock.run(function()
            clock.sleep(1)
            Popup = nil
            redraw()
        end)
    end
    redraw()
end

function redraw()
    screen.clear()
    Pages:redraw()
    if Pages.index == 1 then
        OpPage:redraw()
    elseif Pages.index == 2 then
        FiltPage:redraw()
    elseif Pages.index == 3 then
        MiscPage:redraw()
    end
    if Popup then
        Popup:redraw()
    end
    screen.update()
end

Tab = {}
Tab.__index = Tab

function Tab.new(params, lists, hook)
    local t = {}
    setmetatable(t, Tab)
    t.params = params
    t.lists = lists
    t.hook = hook
    t.index = 1
    return t
end

function Tab:redraw()
    self:hook()
    for _,list in pairs(self.lists) do
        list:redraw()
    end
end

function Tab:enc(n, d)
    if n == 2 then
        self.index = util.clamp(self.index + d, 1, #self.params)
    elseif n == 3 then
        params:delta(self.params[self.index], d)
    end
    redraw()
end

Page = {}
Page.__index = Page

function Page.new(titles, tabs)
    local p = {}
    setmetatable(p, Page)
    p.tabs = tabs
    p.active_tab = 1
    p.ui = UI.Tabs.new(1, titles)
    return p
end

function Page:enc(n, d)
    local tab = self.tabs[self.ui.index]
    tab:enc(n, d)
    redraw()
end

function Page:key(n, z)
    if n == 2 and z == 1 then
        self.ui:set_index_delta(-1, true)
    elseif n == 3 and z == 1 then
        self.ui:set_index_delta(1, true)
    end
    redraw()
end

function Page:redraw()
    self.ui:redraw()
    self.tabs[self.ui.index]:redraw()
end

function enc(n, d)
    if n == 1 then
        Pages:set_index_delta(d, false)
    elseif Pages.index == 1 then
        OpPage:enc(n, d)
    elseif Pages.index == 2 then
        FiltPage:enc(n, d)
    elseif Pages.index == 3 then
        MiscPage:enc(n, d)
    end
    Popup = nil
    redraw()
end

function key(n, z)
    if Pages.index == 1 then
        OpPage:key(n, z)
    elseif Pages.index == 2 then
        FiltPage:key(n, z)
    elseif Pages.index == 3 then
        MiscPage:key(n, z)
    end
    Popup = nil
    redraw()
end
end
