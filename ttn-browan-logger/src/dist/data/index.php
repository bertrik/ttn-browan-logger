<?php
    if ($handle = opendir('.')) {
        $files = [];
        while (false !== ($file = readdir($handle))) {
            if (str_ends_with($file, "csv")) {
                $files[] .= $file;
            }
        }
        closedir($handle);
    }

    rsort($files);
    $list = "";
    foreach ($files as $file) {
        $list .= '<li><a href="'.$file.'">'.$file.'</a></li>';
    }

?>

<html>
<head><link rel="icon" href="favicon.ico"></head>
<body>
<p>CSV-bestanden, per week:</p>
<ul>
<?=$list?>
</ul>
</body>
</html>
