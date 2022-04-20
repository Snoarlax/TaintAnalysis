<?php

$SQLITainted = $_GET['query'];
$SQLITainted = escapeshellcmd($SQLITainted);

mysql_query($SQLITainted);
?>
