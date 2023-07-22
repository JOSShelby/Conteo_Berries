<?php
 include '../../../funciones/php/funcionesGenerales.php';

    $respuestaAJAX = [];
    $bandera=0;

    $arreglo = filter_input(INPUT_GET,"array");

    $arrayAsociativo = json_decode($arreglo);
    // var_dump($arrayAsociativo);
    
    $longitudArr = count($arrayAsociativo);
    // var_dump($longitudArr);

    for($l=0;$l<$longitudArr;$l++){
        print_r("fecha: ".$arrayAsociativo[$l][0]);
        print_r("===========");
        print_r("modulo: ".$arrayAsociativo[$l][1]);
        print_r("===========");
        print_r("estacion: ".$arrayAsociativo[$l][2]);
        print_r("===========");
        print_r("sector: ".$arrayAsociativo[$l][3]);
        print_r("===========");
        print_r("num empleado: ".$arrayAsociativo[$l][4]);
        $letra_a_eliminar = "P ";
        $texto_sin_letra_P = str_replace($letra_a_eliminar, "", $arrayAsociativo[$l][4]);
        print_r("===========");
        print_r("fruto: ".$arrayAsociativo[$l][5]);
        print_r("===========");

        $conexionInsertar = new conectaBase;
        $query = "INSERT INTO cubetascontadasberries (fecha, idmodulo, idestacion, idsector, empleadoid, fruto, status) 
        VALUES ('".$arrayAsociativo[$l][0]."', ".$arrayAsociativo[$l][1].", ".$arrayAsociativo[$l][2].", ".$arrayAsociativo[$l][3].",".$texto_sin_letra_P.", ".$arrayAsociativo[$l][5].",1)";
        print_r($query);
        $conexionInsertar->ejecutaQuerySimple($query);
    }
    // $respuestaAJAX["respuestaArr"]=$arreglo;
    // $respuestaAJAX["statusCode"]=1;
    // echo json_encode($respuestaAJAX);

    
?> 