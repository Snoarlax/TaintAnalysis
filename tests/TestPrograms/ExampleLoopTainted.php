<?php
$a = $_GET['n'];
$b = "Test";
while ($a < 10) {
	$a = $a + 1;
	if ($a ==5) {
		$b = $_GET['str'];
	}
}
echo $b;

?>

