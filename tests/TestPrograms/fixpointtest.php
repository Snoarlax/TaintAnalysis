<?php

$x = $_GET['str'];
$a = 10;

while (true) {
	$x = htmlentities($x);
	if ($a != 0) {
		$x = $_GET['str'];
	}

	else {
		break;
	}
}

echo $x;
