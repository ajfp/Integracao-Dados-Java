xquery version "1.0";
<html>
<body>
<h1>Informacao dos paises Europeus</h1>
<table border="1">
<thead>
<tr>
<th>Pais</th>
<th>ISO</th>
<th>Capital</th>
<th>Presidente</th>
<th>Bandeira</th>
<th>Idiomas</th>
</tr>
</thead>
<tbody>
{
for $paises in doc("paises.xml")/Paises/Pais
let $factos := doc("factos.xml")/Factos/Pais[@ISO = $paises/@ISO]
where $paises/Continente = "Europa"
order by $factos/Area
return
<tr>
<td>{$paises/Nome}</td>
<td>{data($paises/@ISO)}</td>
<td>{$factos/Capital}</td>
<td>{$paises/Nome_Presidente}</td>
<td><img src="{$paises/Link_da_Bandeira}"/></td>
<td>{for $a in $factos/Idiomas/Idioma
return <li>{$a}</li>}</td>
</tr>
}
</tbody></table></body></html>