package com.cometproject.server.network.security;


import java.math.BigInteger;

/**
 * Created by Matty on 10/03/2014.
 */
public class HabboCrypto extends  DiffieHellman {
    private static BigInteger n = new BigInteger("86851DD364D5C5CECE3C883171CC6DDC5760779B992482BD1E20DD296888DF91B33B936A7B93F06D29E8870F703A216257DEC7C81DE0058FEA4CC5116F75E6EFC4E9113513E45357DC3FD43D4EFAB5963EF178B78BD61E81A14C603B24C8BCCE0A12230B320045498EDC29282FF0603BC7B7DAE8FC1B05B52B2F301A9DC783B7", 16);
    private static BigInteger e = new BigInteger("", 3);
    private static BigInteger d = new BigInteger("59AE13E243392E89DED305764BDD9E92E4EAFA67BB6DAC7E1415E8C645B0950BCCD26246FD0D4AF37145AF5FA026C0EC3A94853013EAAE5FF1888360F4F9449EE023762EC195DFF3F30CA0B08B8C947E3859877B5D7DCED5C8715C58B53740B84E11FBC71349A27C31745FCEFEEEA57CFF291099205E230E0C7C27E8E1C0512B", 16);

    public HabboCrypto() {
        super(new BigInteger("114670925920269957593299136150366957983142588366300079186349531", 10), new BigInteger("1589935137502239924254699078669119674538324391752663931735947", 10));


    }
}
