package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import static com.codeborne.selenide.Selenide.open;

public class MoneyTransferTest {

    @BeforeEach
    void shouldOpen() {
        Selenide.open("http://localhost:9999");
    }

    @Test
    void transferMoneyFromSecondCardToFirstCard() {
        var loginPage = new LoginPage();

        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        int beforeBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int beforeBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        var transferPage = dashboardPage.replenishCard(DataHelper.getFirstCardInfo(authInfo));
        transferPage.transferMoney(
                DataHelper.getSecondCardInfo(authInfo).getCardNumber(), 200);
        int afterBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int afterBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        Assertions.assertEquals(beforeBalanceFirstCard + 200, afterBalanceFirstCard);
        Assertions.assertEquals(beforeBalanceSecondCard - 200, afterBalanceSecondCard);
    }

    @Test
    void transferMoneyFromFirstCardToSecondCard() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        int beforeBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int beforeBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        var transferPage = dashboardPage.replenishCard(DataHelper.getSecondCardInfo(authInfo));
        transferPage.transferMoney(
                DataHelper.getFirstCardInfo(authInfo).getCardNumber(), 200);
        int afterBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int afterBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        Assertions.assertEquals(beforeBalanceFirstCard - 200, afterBalanceFirstCard);
        Assertions.assertEquals(beforeBalanceSecondCard + 200, afterBalanceSecondCard);
    }

    @Test
    void transferZeroMoneyFromFirstCardToSecondCard() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        int beforeBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int beforeBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        var transferPage = dashboardPage.replenishCard(DataHelper.getSecondCardInfo(authInfo));
        transferPage.transferMoney(
                DataHelper.getFirstCardInfo(authInfo).getCardNumber(), 0);
        int afterBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int afterBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        Assertions.assertEquals(beforeBalanceFirstCard, afterBalanceFirstCard);
        Assertions.assertEquals(beforeBalanceSecondCard, afterBalanceSecondCard);
    }

    @Test
    void transferOverMoneyFromFirstCardToSecondCard() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var transferPage = dashboardPage.replenishCard(DataHelper.getSecondCardInfo(authInfo));
        transferPage.transferMoney(
                DataHelper.getFirstCardInfo(authInfo).getCardNumber(), 21000);
        transferPage.errorMessage();
    }

    @AfterEach
    void returnToOriginal() {
        var authInfo = DataHelper.getAuthInfo();
        DashboardPage dashboardPage = new DashboardPage();
        int beforeBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int beforeBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        int differenceBetween;
        if (beforeBalanceFirstCard == beforeBalanceSecondCard) {
            return;
        }
        if (beforeBalanceFirstCard > beforeBalanceSecondCard) {
            differenceBetween = beforeBalanceFirstCard - beforeBalanceSecondCard;
            differenceBetween = differenceBetween / 2;
            var transferPage = dashboardPage.replenishCard(DataHelper.getSecondCardInfo(authInfo));
            transferPage.transferMoney(
                    DataHelper.getFirstCardInfo(authInfo).getCardNumber(),
                    differenceBetween);
        } else {
            differenceBetween = beforeBalanceSecondCard - beforeBalanceFirstCard;
            differenceBetween = differenceBetween / 2;
            var transferPage = dashboardPage.replenishCard(DataHelper.getFirstCardInfo(authInfo));
            transferPage.transferMoney(
                    DataHelper.getSecondCardInfo(authInfo).getCardNumber(),
                    differenceBetween);
        }
    }
}
