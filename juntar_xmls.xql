xquery version "1.0";


<resultados>

{for $x in doc("paises.xml")/Paises/Pais,
     $y in doc("factos.xml")/Factos/Pais

where $x/@ISO = $y/@ISO

return <Pais ISO = "{$x/@ISO}">
	<Nome>{data($x/Nome)}</Nome>
	<Capital>{data($y/Capital)}</Capital>
	<Continente>{data($x/Continente)}</Continente>
	<Moeda>{data($y/Moeda)}</Moeda>
	<Populacao>{data($y/Populacao)}</Populacao>
	<Idiomas>{for $aux in $y/Idiomas/Idioma
		return <Idioma>{$aux/text()}</Idioma>}</Idiomas>
</Pais>
}

</resultados>
