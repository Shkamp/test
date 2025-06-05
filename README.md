# Java Console Slot Machine Game

## Overview
This is a fully interactive, console-based slot machine game implemented in Java. It simulates a real-world 5x3 slot machine with 10 symbols, weighted reels, 5 paylines (including V and inverted V shapes), scatter payouts, and a free spins bonus feature. The game is robust, testable, and follows SOLID and DRY principles. It is built with Gradle and is compatible with VS Code.

## Features
- **5x3 grid, 5 reels, 3 rows, 10 symbols**
- **Weighted symbol distribution per reel** (with scatter spacing constraint)
- **5 paylines**: 3 straight, V-shape, and inverted V-shape
- **Scatter symbol**: Pays anywhere, triggers free spins
- **Free spins**: 10 free spins for 3+ scatters, retriggerable
- **Bet selection**: 1, 2, 5, or 10 credits per spin
- **Configurable payout mode**: Pay all winning lines or only the highest line win (see `slotmachine.properties`)
- **JUnit tests**: Covering all major logic and edge cases
- **Gradle build/test**: Modern setup, VS Code compatible

## How to Run
1. **Build and run with Gradle:**
   ```powershell
   ./gradlew run --console=plain
   ```
   or
   ```powershell
   gradle run --console=plain
   ```
2. **Change bet amount**: Use the menu to select your bet before spinning.
3. **Change payout mode**: Edit `slotmachine.properties`:
   - `payAllWins=true` (default): All winning lines are paid.
   - `payAllWins=false`: Only the highest single line win is paid (scatter always pays).

## Design Decisions & Trade-offs
- **Scatter Spacing**: Reels are initialized so that no window of 3 consecutive symbols contains more than one scatter, ensuring fair scatter distribution.
- **Payline Evaluation**: All lines are checked independently, and the payout mode is configurable for flexibility.
- **User Experience**: Winning lines and scatters are highlighted in the grid. A summary of all wins is shown after each spin.
- **Testability**: Core logic is separated from UI, and JUnit tests cover all major and edge cases.
- **Extensibility**: The game can be easily extended with more paylines, symbols, or features by editing configuration or code.

## Interesting Challenges
- **Scatter Spacing Constraint**: Ensuring that no reel can ever show two scatters in a single window required careful symbol placement and shuffling logic.
- **Flexible Payout Logic**: Supporting both "pay all wins" and "pay highest win only" required a clean separation of win evaluation and payout aggregation.
- **Console Highlighting**: Making wins visually clear in a text UI required creative use of symbols and formatting.

## Testing
- **Run all tests:**
   ```powershell
   ./gradlew test
   ```
- **Test coverage includes:**
  - All paylines (including V and inverted V)
  - Scatter payouts
  - Free spins
  - Bet changes
  - Edge cases (min/max balance, multiple wins, scatter spacing)

## Author
*This project was completed as part of a Senior Java Game Developer challenge for Enigma Gaming.*
