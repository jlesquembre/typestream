{ config
, pkgs
, lib
, ...
}:

let
  gradlew =
    pkgs.writeShellScriptBin "gradlew"
      ''
        gradle "$@"
      '';
in
{
  packages = with pkgs; [
    jdk17
    gradle
    gradlew
    protobuf
    # git
    # bashInteractive
    coreutils
    protoc-gen-grpc-web
  ];


  env.PROTOC_GRPC_BIN =
    if pkgs.stdenv.isLinux
    then
      let protobuf-gen-java = (pkgs.callPackage ./protoc-gen-java.nix { }); in
      "${protobuf-gen-java}/bin/protoc-gen-grpc"
    else null;
}
