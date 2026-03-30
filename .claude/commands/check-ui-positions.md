# check-ui-positions

Scan all `assets/ui/*.png` files in the current game and output the measured
libgdx Y coordinates of each visible panel rectangle. Use these values to
position labels/buttons exactly inside the background panel frames.

## Steps

1. Run the scanner:
```bash
GAME_PATH=$(pwd) python3 /home/kaliuzhnyi/asocity/scan_ui_positions.py
```

2. Read the output `ui_positions.json` in the project root.

3. For each screen you are about to write or fix:
   - Find its entry in `ui_positions.json`
   - Use `panels_visual[N].innerBottom` as the minimum Y for content in panel N
   - Use `panels_visual[N].innerTop` as the maximum Y+height for content in panel N
   - Place header label: `headerY = innerTop - headerFontHeight - 5`
   - Place description label: `descY = innerBottom + 6`, `descHeight = headerY - descY - 6`
   - Center a single label: `labelY = centerY - labelHeight/2`
   - Center a button: `btnY = centerY - btnHeight/2`

4. Key formula reminder (libgdx Y-axis is INVERTED vs figma):
```
libgdxY = WORLD_HEIGHT - figmaTopY - elementHeight
```

## Usage example

After running, if `how_to_play` has:
```json
{ "index": 1, "libgdxBottom": 515, "libgdxTop": 663,
  "innerBottom": 523, "innerTop": 655, "centerY": 589 }
```

Code the header at `Y = 655 - 22 - 5 = 628` (22px font + 5px gap from innerTop).
Code the desc at `Y = 523 + 6 = 529`, height = `628 - 529 - 6 = 93`.

## Notes
- Screens with `panels_visual: []` have a uniform background — place actors freely.
- The scanner skips files with `gameplay` in the name (game backgrounds, not UI).
- Re-run after any background image is regenerated.
