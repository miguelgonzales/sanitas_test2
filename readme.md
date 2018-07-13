
Antes de compilar
=================

* Instalar el jar ejecutando desde un terminal la siguiente línea:

mvn install:install-file -Dfile=<ruta del fichero jar> -DgroupId=sanitas.bravo.clientes \
    -DartifactId=sportalclientesweb -Dversion=1.19.0 -Dpackaging=jar