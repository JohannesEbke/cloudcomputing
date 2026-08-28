{
  description = "Cloud Computing";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
    treefmt-nix = {
      url = "github:numtide/treefmt-nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = { self, nixpkgs, treefmt-nix }:

    let
      system = "x86_64-linux";
      treefmtEval = treefmt-nix.lib.evalModule pkgs
        {
          # Not ideal. It would be better to move flake.nix to the git root and use this as the projectRootFile.
          projectRootFile = ".envrc";

          programs = {
            google-java-format.enable = true;
            nixpkgs-fmt.enable = true;
            terraform.enable = true;
          };
        };

      pkgs = import nixpkgs {
        inherit system;
        config.allowUnfree = true;
      };

      mavenExample = pkgs.writeShellApplication {
        name = "mavenExample";
        runtimeInputs = with pkgs; [ maven ];
        text = ''
          mvn clean install
          mvn spring-boot:run
        '';
      };

      gradleExample = pkgs.writeShellApplication {
        name = "gradleExample";
        runtimeInputs = with pkgs; [ gradle ];
        text = ''
          gradle clean build
          gradle bootTestRun
        '';
      };
    in
    {
      apps.${system} = {
        mavenExample = {
          type = "app";
          program = "${mavenExample}/bin/mavenExample";
        };
        gradleExample = {
          type = "app";
          program = "${gradleExample}/bin/gradleExample";
        };
      };

      devShells.${system} = {
        default = pkgs.mkShell {
          buildInputs = with pkgs;
            [
              treefmtEval.config.build.wrapper

              awscli
              docker
              git
              gradle
              gradle-completion
              jdk21_headless
              k9s
              kubectl
              maven
              terraform
            ];

          # Set if you don't want to use a global kubeconfig for this project.
          # KUBECONFIG = "/path/to/your/kubeconfig.yaml";
        };
      };

      formatter.${system} = treefmtEval.config.build.wrapper;
    };
}
