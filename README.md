# Java Console Slot Machine Game

## Overview
This is a fully interactive, console-based slot machine game implemented in Java. It simulates a real-world 5x3 slot machine with 10 symbols, weighted reels, 5 paylines (including V and inverted V shapes), scatter payouts, and a free spins bonus feature. The game is robust, testable, and follows SOLID and DRY principles. It is built with Gradle and is compatible with VS Code.

## Features
- **5x3 grid, 10 symbols, weighted reels**
- **Configurable symbol distribution, paylines, and scatter distance** via `slotmachine.properties`
- **5 paylines**: 3 straight, V-shape, and inverted V-shape
- **Scatter symbol**: Pays anywhere, triggers free spins (with retriggers)
- **Configurable payout mode**: Pay all winning lines or only the highest (see `slotmachine.properties`)
- **Modern, user-friendly console UI**
- **Fully tested with JUnit 5**
- **SOLID/DRY design**, extensible and maintainable

## Configuration
See `slotmachine.properties` for all options:
- `payAllWins`: Pay all winning lines or only the highest
- `symbols`: Symbol distribution per reel
- `paylines`: Payline definitions
- `minScatterDistance`: Minimum distance between scatters on a reel

### Scatter Spacing Logic

The `minScatterDistance` property in `slotmachine.properties` controls the minimum number of positions between any two scatter symbols on a reel. This prevents clusters of scatters and ensures fair, random distribution. If the requested number of scatters and minimum distance cannot fit on a reel, the game will throw a configuration error at startup. This logic is enforced in code and tested in the unit tests for reliability and game balance.

## Scatter Spacing Constraint
- The game enforces a minimum distance between Scatter symbols on each reel, ensuring that no spin can show more than one Scatter in the visible 3-symbol window of a reel. This is achieved by spacing out Scatters during reel construction, and is configurable via `minScatterDistance` in `slotmachine.properties`. This constraint is critical for fair gameplay and is strictly enforced in both code and tests.

## Running & Building
- **Build**: `gradle build`
- **Run**: `gradle run` or run `Main` in your IDE
- **Test**: `gradle test`
- **Debug**: Use VS Code tasks/launch configs for Gradle test debugging

## Code Structure
- `Main.java`: Console UI and game loop
- `SlotMachine.java`: Core game logic, configuration, payouts
- `Reel.java`: Reel construction, symbol distribution, scatter placement
- `Symbol.java`: Enum for all symbols and payouts
- `SlotMachineTest.java`: Comprehensive JUnit 5 tests

## Documentation
- All classes and public methods are documented with Javadoc
- All configuration options are documented in `slotmachine.properties`
- See code for further inline documentation

## Design Notes
- Follows SOLID and DRY principles
- Extensible for new features, symbols, or payout logic
- All configuration is externalized for easy tuning

## License
MIT (or specify your license)

## Author
*This project was completed as part of a Senior Java Game Developer challenge for Enigma Gaming.*
