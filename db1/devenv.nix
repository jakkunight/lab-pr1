{
  pkgs,
  lib,
  config,
  inputs,
  ...
}:
let
  oracledb = inputs.nix-oracle-db.packages.${pkgs.stdenv.hostPlatform.system};
in

{
  # imports = [
  #   ./oracledb.nix
  # ];
  # https://devenv.sh/basics/
  env = {
    CLASSPATH = "${config.env.DEVENV_ROOT}/:${config.env.DEVENV_ROOT}/drivers/ojdbc17.jar";

  };

  # https://devenv.sh/packages/
  packages = with pkgs; [
    git
    google-java-format
    gvfs
    glibc
    busybox
    # oracle-instantclient
    oracledb.oracle-database
  ];

  # https://devenv.sh/languages/
  # languages.rust.enable = true;
  languages = {
    java = {
      enable = true;
      jdk.package = pkgs.openjdk;
    };
  };

  # https://devenv.sh/processes/
  process.manager.implementation = "mprocs";
  processes = {
  };

  # https://devenv.sh/services/
  services = {
    # oracledb = {
    #   enable = true;
    # };
  };

  # https://devenv.sh/scripts/
  scripts = {
    # oracledb-start = {
    #   exec = "${oracledb.oracle-database}/etc/init.d/oracle-free-23c start";
    # };
    # oracledb-configure = {
    #   exec = "${oracledb.oracle-database}/etc/init.d/oracle-free-23c configure";
    # };
    # Script to start the Podman container:
    start-oracle = {
      exec = "podman run -d -p 127.0.0.1:1521:1521 -e ORACLE_PASSWORD=oracle -v oracledb:/opt/oracle/oradata gvenzl/oracle-free";
    };
  };

  # https://devenv.sh/basics/
  enterShell = "";

  # https://devenv.sh/tasks/
  # tasks = {
  #   "myproj:setup".exec = "mytool build";
  #   "devenv:enterShell".after = [ "myproj:setup" ];
  # };

  # https://devenv.sh/tests/
  enterTest = "";

  # https://devenv.sh/git-hooks/
  # git-hooks.hooks.shellcheck.enable = true;

  # See full reference at https://devenv.sh/reference/options/
}
