MACHINE DAW — UI reference kit
================================
Landscape phone, 2400 × 1080 (20:9). Built as implementation
reference for the native Android DAW described in UI_PLAN.md,
SDD.md, and SSD.md.

Full PNG screens live in this folder (00–23). Upload may be
incremental; prefer the repo zip release asset if images are missing.

Chrome is identical across machine screens:
  status → transport → FX rail + tab strip → machine header
  (identity, preset/kit, Perform·Shape·Write) → layer body →
  page-turn corner (inset, bottom-right).

Color follows a machine everywhere (tab, header, mixer strip,
arrangement row, FX). Color is never the only cue: type icon,
and an instance-number badge when a type is duplicated.

Design notes
------------
- Phone-first landscape, no rack metaphor. Tabs are navigation.
- Content swipe changes layer, not machine.
- FX button never disappears; it disables on Mixer and Song.
- Page-turn is inset from the system back-gesture zone.
- Presets are a library, not embedded in the project file.
- BeatBox “preset” is labeled Kit; same browser mechanism.
- Caps: 14 machines, 2 FX slots, 4 macros, 8 pads, patterns A–H.

Brand chrome stays steel/ink. Play green and Record red are
semantic, not brand. Machine hues are identity only.
