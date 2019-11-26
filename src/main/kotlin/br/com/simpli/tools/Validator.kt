package br.com.simpli.tools

import java.util.InputMismatchException
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *
 * @author ricardoprado
 */
object Validator {

    /**
     * Formats using (xx) xxxxx-xxxx
     * @param celNumber
     * @return
     */

    fun formatCelPhone(celNumber: String): String {
        var tCelNumber = celNumber
        tCelNumber = "(" + tCelNumber.substring(0, 2) + ")" + tCelNumber.substring(2, tCelNumber.length)
        tCelNumber = tCelNumber.substring(0, 4) + tCelNumber.substring(4, 9) + "-" + tCelNumber.substring(9, tCelNumber.length)
        return tCelNumber
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val patStr = "^\\+?[1-9]\\d{10,14}$"

        pattern = Pattern.compile(patStr)
        matcher = pattern.matcher(phoneNumber)
        return matcher.matches()
    }

    //Ex Celulares validos : 11996988588 , 011996988588 , 11196988588 , 5511996988588 , +5511996988588
    //Ex Celulares invalidos : 11096988588 , 01996988588 , 11996899889899898989 , +0511996988588 , 0511996988588
    fun isValidCelPhoneNumber(phone: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val patStr = "^(\\+?[1-9]{2})?([1-9]{2}|0[1-9]{2})[1-9][0-9]{8}$"

        pattern = Pattern.compile(patStr)
        matcher = pattern.matcher(phone)
        return matcher.matches()

    }

    fun isEmail(email: String?): Boolean {
        if (email == null) {
            return false
        }

        val pattern: Pattern
        val matcher: Matcher
        val patStr = "^[a-zA-Z0-9.!#\$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*\$"

        pattern = Pattern.compile(patStr)
        matcher = pattern.matcher(email)

        return matcher.matches()
    }

    fun isCorporativeEmail(email: String): Boolean {
        if (email.indexOf("gmail.com") != -1) {
            return false
        } else if (email.indexOf("yahoo.com") != -1) {
            return false
        } else if (email.indexOf("yahoo.com.br") != -1) {
            return false
        } else if (email.indexOf("ymail.com") != -1) {
            return false
        } else if (email.indexOf("rocketmail.com") != -1) {
            return false
        } else if (email.indexOf("bol.com.br") != -1) {
            return false
        } else if (email.indexOf("hotmail.com") != -1) {
            return false
        } else if (email.indexOf("hotmail.com.br") != -1) {
            return false
        } else if (email.indexOf("live.com") != -1) {
            return false
        } else if (email.indexOf("msn.com") != -1) {
            return false
        } else if (email.indexOf("ig.com.br") != -1) {
            return false
        } else if (email.indexOf("globomail.com.br") != -1) {
            return false
        } else if (email.indexOf("oi.com.br") != -1) {
            return false
        } else if (email.indexOf("pop.com.br") != -1) {
            return false
        } else if (email.indexOf("inteligweb.com.br") != -1) {
            return false
        } else if (email.indexOf("folha.com.br") != -1) {
            return false
        } else if (email.indexOf("zipmail.com.br") != -1) {
            return false
        }
        return true
    }

    fun isCPF(pCPF: String?): Boolean {
        var CPF: String = pCPF ?: return false

        CPF = CPF.replace("-", "")
        CPF = CPF.replace(".", "")

        // considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (CPF == "00000000000" || CPF == "11111111111" ||
                CPF == "22222222222" || CPF == "33333333333" ||
                CPF == "44444444444" || CPF == "55555555555" ||
                CPF == "66666666666" || CPF == "77777777777" ||
                CPF == "88888888888" || CPF == "99999999999" ||
                CPF.length != 11)
            return false

        val dig10: Char
        val dig11: Char
        var sm: Int
        var i: Int
        var r: Int
        var num: Int
        var peso: Int

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0
            peso = 10
            i = 0
            while (i < 9) {
                // converte o i-esimo caractere do CPF em um numero:
                // por exemplo, transforma o caractere '0' no inteiro 0
                // (48 eh a posicao de '0' na tabela ASCII)
                num = (CPF[i].toInt() - 48).toInt()
                sm += num * peso
                peso -= 1
                i++
            }

            r = 11 - sm % 11
            if (r == 10 || r == 11)
                dig10 = '0'
            else
                dig10 = (r + 48).toChar() // converte no respectivo caractere numerico

            // Calculo do 2o. Digito Verificador
            sm = 0
            peso = 11
            i = 0
            while (i < 10) {
                num = (CPF[i].toInt() - 48).toInt()
                sm += num * peso
                peso -= 1
                i++
            }

            r = 11 - sm % 11
            if (r == 10 || r == 11)
                dig11 = '0'
            else
                dig11 = (r + 48).toChar()

            // Verifica se os digitos calculados conferem com os digitos informados.
            return dig10 == CPF[9] && dig11 == CPF[10]
        } catch (erro: InputMismatchException) {
            return false
        }

    }

    fun isCNPJ(pCNPJ: String): Boolean {
        var CNPJ = pCNPJ
        var saida: Boolean
        var formatCNPJ = CNPJ.replace(".", "")
        formatCNPJ = formatCNPJ.replace("/", "")
        formatCNPJ = formatCNPJ.replace("-", "")

        CNPJ = formatCNPJ
        if (CNPJ == "00000000000000"
                || CNPJ == "11111111111111"
                || CNPJ == "22222222222222"
                || CNPJ == "33333333333333"
                || CNPJ == "44444444444444"
                || CNPJ == "55555555555555"
                || CNPJ == "66666666666666"
                || CNPJ == "77777777777777"
                || CNPJ == "88888888888888"
                || CNPJ == "99999999999999"
                || CNPJ.length != 14) {
            saida = false
        } else {
            val dig13: Char
            val dig14: Char
            var sm: Int
            var i: Int
            var r: Int
            var num: Int
            var peso: Int

            try {
                sm = 0
                peso = 2
                i = 11
                while (i >= 0) {
                    num = (CNPJ[i].toInt() - 48).toInt()
                    sm += num * peso
                    peso += 1
                    if (peso == 10) {
                        peso = 2
                    }
                    i--
                }

                r = sm % 11
                if (r == 0 || r == 1) {
                    dig13 = '0'
                } else {
                    dig13 = (11 - r + 48).toChar()
                }

                sm = 0
                peso = 2
                i = 12
                while (i >= 0) {
                    num = (CNPJ[i].toInt() - 48).toInt()
                    sm += num * peso
                    peso += 1
                    if (peso == 10) {
                        peso = 2
                    }
                    i--
                }

                r = sm % 11
                if (r == 0 || r == 1) {
                    dig14 = '0'
                } else {
                    dig14 = (11 - r + 48).toChar()
                }

                saida = dig13 == CNPJ[12] && dig14 == CNPJ[13]
            } catch (ex: Exception) {
                saida = false
            }

        }

        return saida

    }

}
