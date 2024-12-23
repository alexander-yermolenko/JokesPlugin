# JokesPlugin

**JokesPlugin** is a fun Minecraft plugin that allows players to interact with jokes, add new jokes, and enjoy a variety of humorous content within the game. The plugin also includes features like cooldowns, custom sounds, joke categories, and more to make the experience enjoyable for everyone!

## Screenshots

![Plugin Screenshot1](src/main/images/1.png)
![Plugin Screenshot2](src/main/images/2.png)

## Features

- **/joke**: Get a random joke.
- **/joke add <joke>**: Add a new joke to the list.

## Installation

1. Download the latest version of **JokesPlugin**.
2. Place the plugin `.jar` file into your server's `plugins/` folder.
3. Restart or reload your server.
4. (Optional) Modify the `config.yml` to configure jokes, cooldowns, sound effects, and other settings.

## Configuration

### `config.yml`

```yaml
jokes:
  - "Why don't skeletons fight each other? They don't have the guts!"
  - "Why did the Creeper cross the road? To blow up the chicken!"
  - "Knock knock. Who’s there? Cow says. Cow says who? No silly, cow says moo!"
  - "What do you call a Minecraft bed? A block of comfort!"
  - "Why did the chicken join Minecraft? To lay eggs in the game world!"
cooldown-seconds: 10
max-joke-length: 100
