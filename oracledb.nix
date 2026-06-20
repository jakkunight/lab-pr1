{
  inputs,
  pkgs,
  config,
  lib,
  ...
}:
let
  # Shorthand for the option name.
  cfg = config.services.oracledb;
  # Escape shell args.
  q = lib.escapeShellArg;
  # Runtime directory to execute the programs.
  runtimeDir = "${config.env.DEVENV_RUNTIME}/oracledb";
  # Parser for listening addresses.
  parseListenAddresses = (
    input:
    let
      convertSpecialValue =
        value:
        if value == "*" || value == "0.0.0.0" then
          "127.0.0.1"
        else if value == "::" then
          "::1"
        else
          value;
    in
    lib.pipe input [
      (lib.splitString ",")
      (map lib.trim)
      (map convertSpecialValue)
      (builtins.filter (x: x != ""))
    ]
  );
  oracledb = inputs.nix-oracle-db.packages.${pkgs.stdenv.hostPlatform.system};
in
{
  options = {
    services.oracledb = {
      enable = lib.mkEnableOption "Enable the Oracle DB XE service.";
      address = lib.mkOption {
        type = lib.types.str;
        description = "The listening address of the server. Defaults to 'localhost' (127.0.0.1)";
        default = "127.0.0.1";
      };
      port = lib.mkOption {
        type = lib.types.port;
        description = "The listening port for the server. Defaults to 1521";
        default = 1521;
      };
      user = lib.mkOption {
        type = lib.types.nonEmptyStr;
        description = "The user of the database";
        default = "oracle";
      };
      # package = lib.mkOption {
      #   description = "The package to be used. By default uses the Drupol's flake.";
      #   type = lib.types.package;
      #   default = oracledb.oracle-database;
      # };
    };
  };
  config = lib.mkIf cfg.enable {
    packages = [
      oracledb.oracle-database
    ];
    env = {

    };
    processes.oracledb = {
      ports.main.allocate = cfg.port;
      exec = ''
        ${oracledb.oracle-database}/etc/init.d/oracle-free-*c start
      '';
    };
  };
}
