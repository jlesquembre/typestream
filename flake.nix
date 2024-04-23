{
  description = "My flake";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    devenv = {
      url = "github:cachix/devenv";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = { self, nixpkgs, flake-utils, devenv, ... }@inputs:
    flake-utils.lib.eachDefaultSystem
      (system:
        let
          pkgs = (import nixpkgs {
            inherit system;
          });

        in
        {
          devShells = {
            default = devenv.lib.mkShell
              {
                inherit inputs pkgs;
                modules = [
                  ./nix/shell.nix
                ];
              };
          };
        });
}
