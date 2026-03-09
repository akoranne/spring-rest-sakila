package com.example.app.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Country {
    NONE(0, Constants.NONE),
    AFGHANISTAN(1, Constants.AFGHANISTAN),
    ALGERIA(2, Constants.ALGERIA),
    AMERICAN_SAMOA(3, Constants.AMERICAN_SAMOA),
    ANGOLA(4, Constants.ANGOLA),
    ANGUILLA(5, Constants.ANGUILLA),
    ARGENTINA(6, Constants.ARGENTINA),
    ARMENIA(7, Constants.ARMENIA),
    AUSTRALIA(8, Constants.AUSTRALIA),
    AUSTRIA(9, Constants.AUSTRIA),
    AZERBAIJAN(10, Constants.AZERBAIJAN),
    BAHRAIN(11, Constants.BAHRAIN),
    BANGLADESH(12, Constants.BANGLADESH),
    BELARUS(13, Constants.BELARUS),
    BOLIVIA(14, Constants.BOLIVIA),
    BRAZIL(15, Constants.BRAZIL),
    BRUNEI(16, Constants.BRUNEI),
    BULGARIA(17, Constants.BULGARIA),
    CAMBODIA(18, Constants.CAMBODIA),
    CAMEROON(19, Constants.CAMEROON),
    CANADA(20, Constants.CANADA),
    CHAD(21, Constants.CHAD),
    CHILE(22, Constants.CHILE),
    CHINA(23, Constants.CHINA),
    COLOMBIA(24, Constants.COLOMBIA),
    CONGO_THE_DEMOCRATIC_REPUBLIC_OF_THE(25, Constants.CONGO_THE_DEMOCRATIC_REPUBLIC_OF_THE),
    CZECH_REPUBLIC(26, Constants.CZECH_REPUBLIC),
    DOMINICAN_REPUBLIC(27, Constants.DOMINICAN_REPUBLIC),
    ECUADOR(28, Constants.ECUADOR),
    EGYPT(29, Constants.EGYPT),
    ESTONIA(30, Constants.ESTONIA),
    ETHIOPIA(31, Constants.ETHIOPIA),
    FAROE_ISLANDS(32, Constants.FAROE_ISLANDS),
    FINLAND(33, Constants.FINLAND),
    FRANCE(34, Constants.FRANCE),
    FRENCH_GUIANA(35, Constants.FRENCH_GUIANA),
    FRENCH_POLYNESIA(36, Constants.FRENCH_POLYNESIA),
    GAMBIA(37, Constants.GAMBIA),
    GERMANY(38, Constants.GERMANY),
    GREECE(39, Constants.GREECE),
    GREENLAND(40, Constants.GREENLAND),
    HOLY_SEE_VATICAN_CITY_STATE(41, Constants.HOLY_SEE_VATICAN_CITY_STATE),
    HONG_KONG(42, Constants.HONG_KONG),
    HUNGARY(43, Constants.HUNGARY),
    INDIA(44, Constants.INDIA),
    INDONESIA(45, Constants.INDONESIA),
    IRAN(46, Constants.IRAN),
    IRAQ(47, Constants.IRAQ),
    ISRAEL(48, Constants.ISRAEL),
    ITALY(49, Constants.ITALY),
    JAPAN(50, Constants.JAPAN),
    KAZAKHSTAN(51, Constants.KAZAKHSTAN),
    KENYA(52, Constants.KENYA),
    KUWAIT(53, Constants.KUWAIT),
    LATVIA(54, Constants.LATVIA),
    LIECHTENSTEIN(55, Constants.LIECHTENSTEIN),
    LITHUANIA(56, Constants.LITHUANIA),
    MADAGASCAR(57, Constants.MADAGASCAR),
    MALAWI(58, Constants.MALAWI),
    MALAYSIA(59, Constants.MALAYSIA),
    MEXICO(60, Constants.MEXICO),
    MOLDOVA(61, Constants.MOLDOVA),
    MOROCCO(62, Constants.MOROCCO),
    MOZAMBIQUE(63, Constants.MOZAMBIQUE),
    MYANMAR(64, Constants.MYANMAR),
    NAURU(65, Constants.NAURU),
    NEPAL(66, Constants.NEPAL),
    NETHERLANDS(67, Constants.NETHERLANDS),
    NEW_ZEALAND(68, Constants.NEW_ZEALAND),
    NIGERIA(69, Constants.NIGERIA),
    NORTH_KOREA(70, Constants.NORTH_KOREA),
    OMAN(71, Constants.OMAN),
    PAKISTAN(72, Constants.PAKISTAN),
    PARAGUAY(73, Constants.PARAGUAY),
    PERU(74, Constants.PERU),
    PHILIPPINES(75, Constants.PHILIPPINES),
    POLAND(76, Constants.POLAND),
    PUERTO_RICO(77, Constants.PUERTO_RICO),
    ROMANIA(78, Constants.ROMANIA),
    REUNION(79, Constants.REUNION),
    RUSSIAN_FEDERATION(80, Constants.RUSSIAN_FEDERATION),
    SAINT_VINCENT_AND_THE_GRENADINES(81, Constants.SAINT_VINCENT_AND_THE_GRENADINES),
    SAUDI_ARABIA(82, Constants.SAUDI_ARABIA),
    SENEGAL(83, Constants.SENEGAL),
    SLOVAKIA(84, Constants.SLOVAKIA),
    SOUTH_AFRICA(85, Constants.SOUTH_AFRICA),
    SOUTH_KOREA(86, Constants.SOUTH_KOREA),
    SPAIN(87, Constants.SPAIN),
    SRI_LANKA(88, Constants.SRI_LANKA),
    SUDAN(89, Constants.SUDAN),
    SWEDEN(90, Constants.SWEDEN),
    SWITZERLAND(91, Constants.SWITZERLAND),
    TAIWAN(92, Constants.TAIWAN),
    TANZANIA(93, Constants.TANZANIA),
    THAILAND(94, Constants.THAILAND),
    TONGA(95, Constants.TONGA),
    TUNISIA(96, Constants.TUNISIA),
    TURKEY(97, Constants.TURKEY),
    TURKMENISTAN(98, Constants.TURKMENISTAN),
    TUVALU(99, Constants.TUVALU),
    UKRAINE(100, Constants.UKRAINE),
    UNITED_ARAB_EMIRATES(101, Constants.UNITED_ARAB_EMIRATES),
    UNITED_KINGDOM(102, Constants.UNITED_KINGDOM),
    UNITED_STATES(103, Constants.UNITED_STATES),
    VENEZUELA(104, Constants.VENEZUELA),
    VIETNAM(105, Constants.VIETNAM),
    VIRGIN_ISLANDS_US(106, Constants.VIRGIN_ISLANDS_US),
    YEMEN(107, Constants.YEMEN),
    YUGOSLAVIA(108, Constants.YUGOSLAVIA),
    ZAMBIA(109, Constants.ZAMBIA);

    public static final Map<Integer, Country> ID_MAP = Stream.of(Country.values())
            .collect(Collectors.toUnmodifiableMap(Country::getId, Function.identity()));
    public static final Map<String, Country> COUNTRY_MAP = Stream.of(Country.values())
            .collect(Collectors.toUnmodifiableMap(Country::getCountry, Function.identity()));

    private final Integer id;
    private final String country;

    /**
     * Replace last country id in string to country name.
     *
     * @param str string with country id
     * @return string with country name
     */
    public static String replaceCountryIdToCountryInString(String str) {
        final var pattern = Pattern.compile("\\d+$");
        final var matcher = pattern.matcher(str);
        if (matcher.find()) {
            final var lastElement = matcher.group();
            final var country = ID_MAP.get(Integer.parseInt(lastElement));
            str = str.replaceAll("\\d+$", country.getCountry());
        }
        return str;
    }

    public static class Constants {
        public static final String NONE = "-";
        public static final String AFGHANISTAN = "Afghanistan";
        public static final String ALGERIA = "Algeria";
        public static final String AMERICAN_SAMOA = "American Samoa";
        public static final String ANGOLA = "Angola";
        public static final String ANGUILLA = "Anguilla";
        public static final String ARGENTINA = "Argentina";
        public static final String ARMENIA = "Armenia";
        public static final String AUSTRALIA = "Australia";
        public static final String AUSTRIA = "Austria";
        public static final String AZERBAIJAN = "Azerbaijan";
        public static final String BAHRAIN = "Bahrain";
        public static final String BANGLADESH = "Bangladesh";
        public static final String BELARUS = "Belarus";
        public static final String BOLIVIA = "Bolivia";
        public static final String BRAZIL = "Brazil";
        public static final String BRUNEI = "Brunei";
        public static final String BULGARIA = "Bulgaria";
        public static final String CAMBODIA = "Cambodia";
        public static final String CAMEROON = "Cameroon";
        public static final String CANADA = "Canada";
        public static final String CHAD = "Chad";
        public static final String CHILE = "Chile";
        public static final String CHINA = "China";
        public static final String COLOMBIA = "Colombia";
        public static final String CONGO_THE_DEMOCRATIC_REPUBLIC_OF_THE = "Congo, The Democratic Republic of the";
        public static final String CZECH_REPUBLIC = "Czech Republic";
        public static final String DOMINICAN_REPUBLIC = "Dominican Republic";
        public static final String ECUADOR = "Ecuador";
        public static final String EGYPT = "Egypt";
        public static final String ESTONIA = "Estonia";
        public static final String ETHIOPIA = "Ethiopia";
        public static final String FAROE_ISLANDS = "Faroe Islands";
        public static final String FINLAND = "Finland";
        public static final String FRANCE = "France";
        public static final String FRENCH_GUIANA = "French Guiana";
        public static final String FRENCH_POLYNESIA = "French Polynesia";
        public static final String GAMBIA = "Gambia";
        public static final String GERMANY = "Germany";
        public static final String GREECE = "Greece";
        public static final String GREENLAND = "Greenland";
        public static final String HOLY_SEE_VATICAN_CITY_STATE = "Holy See (Vatican City State)";
        public static final String HONG_KONG = "Hong Kong";
        public static final String HUNGARY = "Hungary";
        public static final String INDIA = "India";
        public static final String INDONESIA = "Indonesia";
        public static final String IRAN = "Iran";
        public static final String IRAQ = "Iraq";
        public static final String ISRAEL = "Israel";
        public static final String ITALY = "Italy";
        public static final String JAPAN = "Japan";
        public static final String KAZAKHSTAN = "Kazakstan";
        public static final String KENYA = "Kenya";
        public static final String KUWAIT = "Kuwait";
        public static final String LATVIA = "Latvia";
        public static final String LIECHTENSTEIN = "Liechtenstein";
        public static final String LITHUANIA = "Lithuania";
        public static final String MADAGASCAR = "Madagascar";
        public static final String MALAWI = "Malawi";
        public static final String MALAYSIA = "Malaysia";
        public static final String MEXICO = "Mexico";
        public static final String MOLDOVA = "Moldova";
        public static final String MOROCCO = "Morocco";
        public static final String MOZAMBIQUE = "Mozambique";
        public static final String MYANMAR = "Myanmar";
        public static final String NAURU = "Nauru";
        public static final String NEPAL = "Nepal";
        public static final String NETHERLANDS = "Netherlands";
        public static final String NEW_ZEALAND = "New Zealand";
        public static final String NIGERIA = "Nigeria";
        public static final String NORTH_KOREA = "North Korea";
        public static final String OMAN = "Oman";
        public static final String PAKISTAN = "Pakistan";
        public static final String PARAGUAY = "Paraguay";
        public static final String PERU = "Peru";
        public static final String PHILIPPINES = "Philippines";
        public static final String POLAND = "Poland";
        public static final String PUERTO_RICO = "Puerto Rico";
        public static final String ROMANIA = "Romania";
        public static final String REUNION = "Réunion";
        public static final String RUSSIAN_FEDERATION = "Russian Federation";
        public static final String SAINT_VINCENT_AND_THE_GRENADINES = "Saint Vincent and the Grenadines";
        public static final String SAUDI_ARABIA = "Saudi Arabia";
        public static final String SENEGAL = "Senegal";
        public static final String SLOVAKIA = "Slovakia";
        public static final String SOUTH_AFRICA = "South Africa";
        public static final String SOUTH_KOREA = "South Korea";
        public static final String SPAIN = "Spain";
        public static final String SRI_LANKA = "Sri Lanka";
        public static final String SUDAN = "Sudan";
        public static final String SWEDEN = "Sweden";
        public static final String SWITZERLAND = "Switzerland";
        public static final String TAIWAN = "Taiwan";
        public static final String TANZANIA = "Tanzania";
        public static final String THAILAND = "Thailand";
        public static final String TONGA = "Tonga";
        public static final String TUNISIA = "Tunisia";
        public static final String TURKEY = "Turkey";
        public static final String TURKMENISTAN = "Turkmenistan";
        public static final String TUVALU = "Tuvalu";
        public static final String UKRAINE = "Ukraine";
        public static final String UNITED_ARAB_EMIRATES = "United Arab Emirates";
        public static final String UNITED_KINGDOM = "United Kingdom";
        public static final String UNITED_STATES = "United States";
        public static final String VENEZUELA = "Venezuela";
        public static final String VIETNAM = "Vietnam";
        public static final String VIRGIN_ISLANDS_US = "Virgin Islands, U.S.";
        public static final String YEMEN = "Yemen";
        public static final String YUGOSLAVIA = "Yugoslavia";
        public static final String ZAMBIA = "Zambia";
    }
}
