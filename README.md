# EXA (The WonderEvents Example Expansion)
EXA (or wonderEXAmple/WEX) is a small extension included when you use WonderEvents; it will be added by default during your first installation of WE.

Don't worry! You can easily remove it, WonderEvents doesn't depend on EXA to work <3

A list of its sample features:
- Upon loading, log a message to the console stating that this is the
  example expansion and that it can be safely deleted.
- Welcome (and register in `data/wex_db.yml`) any player
  that EXA did not yet know.
- When the server skips the night because everyone is sleeping
  (`TimeSkipEvent` with `SkipReason.NIGHT_SKIP`), it sends all
  online players a "good morning" message.
- Adds the `/exa` command, which responds with a fixed message.

Made with lots of love, Voiid Studios Team <3
