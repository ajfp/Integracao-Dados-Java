(for $factos in doc("factos.xml")/Factos/Pais
let $paises := doc("paises.xml")/Paises/Pais[@ISO = $factos/@ISO]
order by $factos/Populacao descending
return ("Pais: ", $paises/Nome, "&#10;"))[position() le 15]