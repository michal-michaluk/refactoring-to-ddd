
Poprawa jakości testów
----------------------

1. oceń klasę testową `tools.ShortageFinderTest`
- jakie wady problemy dostrzegasz?
- jakie wzorce, techniki i sztuczki proponujesz?

2. wprowadź własny Assert Object dla `List<ShortageEntity> shortages`
- zaproponuj wygodne API
- wewnętrznie warto użyć AssertJ (jest w classpath)

3. popraw fabrykę danych testowych dla `List<DemandEntity> demands`
- przenieś metody do nowej klasy
- jakie informacje chcemy uwypuklić w API fabryki
- czy lepszy jest builder?

4. rozdziel given i when
- ukryj wywołanie metody pod testami w prywatnej metodie testu
- dane testowe przekaż przez pola klasy, a nie przez parametry metody
- czy warto wydzielić do osobnej klasy nowe pola i metodę?

5. zaproponuj prostrze scenariusze testowe
- może są jakieś bug-i?
 
6. wprowadź buildera dla `List<ProductionEntity> productions`
- jak nazwać reprezentanta 'planu produkcji', który był w teście?
- zerknij do implementacji `tools.ShortageFinder`, jakie dane są najistotniejsze?
- zaproponuj API fabryki / buildera
- wydziel fabryki / buildera do nowych klas

7. który design testów lepiej zaapsorbuje zmiany podczas refactoring


Refactoring architektury
------------------------

1. czego nauczyliśmy się o kodzie produkcyjnym po testowaniu eksploracyjnym
- jakie dane z wejścia są istotne a jakie nie?
- które dane zmieniają się w kontekście obliczeń pod testami?
- jakie byty można by zareprezentować jako obiekty (zachowania + enkapsulacja)?  


2. Coding Dojo Refactoring
- refaktoryzacja do modelu domeny
- refaktoryzacja do łatwo testowalnej architektury hexagonalnej 
