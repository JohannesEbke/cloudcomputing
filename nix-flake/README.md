This nix flake was only tested on an x86_64-linux plattform. Support for other platforms may or may not work.
This flake provides no packages, but example apps for building the starter maven and gradle projects are provided.
(Providing maven packages might be straight forward, while gradle seems to be a little more difficult.)

Get started by installing [nix](https://nixos.org/download/), enabling [nix flakes](https://nixos.wiki/wiki/Flakes) and running `nix develop` in this directory.
You should now have all tools you need for this project.

To run the example maven app:
 - Move to `00-einführung/uebung/jdk-test-1/`
 - Run `nix run ../../../nix-flake/#mavenExample` 

To run the example gradle app:
 - Move to `00-einführung/uebung/jdk-test-2/`
 - Run `nix run ../../../nix-flake/#gradleExample`

Alternatively have a look at `flake.nix` to find out what these simple scripts do and run the commands yourself!

This flake also supports [nix-direnv](https://github.com/nix-community/nix-direnv) which automatically enables the nix development shell when you enter the project.

It also comes with a [treefmt-nix](https://github.com/numtide/treefmt-nix) configuration, for formatting java, nix and terraform files.
This is however not currently configured to be compatible with the prevailing code style.
