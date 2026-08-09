# Google Dino Jump Game

## Background
My first Java application project (December 2023), inspired by Google Chrome's Dino Game.

Through this project, I explored GUI programming, game logic, and object-oriented design using Java Swing.

## Overview
This project is a 2D jumping game where players control a dinosaur character to avoid obstacles, collect coins, and achieve high scores.

The player controls the dinosaur using the keyboard. The game continues until the dinosaur collides with an obstacle. Coins can be collected to increase the score, while sunflowers provide a speed boost effect.

## Features
- Keyboard-controlled dinosaur jumping
- Random obstacle generation
- Collision detection between the dinosaur and obstacles
- Coin collection and score tracking
- Speed boost mechanism through sunflower collection
- High score management
- Game restart functionality

## Controls
Pressing the spacebar allows the dinosaur to jump over obstacles. The game ends when the dinosaur collides with an obstacle.

## Object-Oriented Design
The project is organized using multiple classes:

### Jumpx (Main Class)
- Extends `JFrame` and implements `ActionListener` and `KeyListener`
- Controls the main game window and game states
- Handles user input, rendering, timers, and game updates

### Obstacle
- Represents barriers that the dinosaur needs to avoid
- Controls obstacle size, position, and movement speed

### Star (Coin)
- Represents collectible coins
- Handles coin movement and collision with the player

### Moon (Sunflower)
- Represents speed boost items
- Increases game speed after collection

### HighScoreList
- Stores and manages high scores using an ArrayList

## Implementation Details
The game uses:
- Java Swing for GUI development
- `Timer` for the real-time game loop
- `KeyListener` for keyboard interaction
- `paint()` method for rendering game elements
- Collision detection using object boundaries

The main game controller uses timers to:
- Generate obstacles, coins, and sunflower items
- Update the score
- Maintain continuous gameplay
