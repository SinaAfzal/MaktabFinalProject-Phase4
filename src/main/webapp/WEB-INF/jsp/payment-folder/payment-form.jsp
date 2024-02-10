<%--
  Created by IntelliJ IDEA.
  User: Sina
  Date: 2/3/2024
  Time: 7:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>Title</title>
    <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
            rel="stylesheet"
    />
    <link href="css/payment-form.css" />
    <script src="https://www.google.com/recaptcha/api.js"></script>
</head>
<body style="background-color: lightskyblue">
<div class="timer-container" style="display: flex;flex-direction: column;width: 30%;margin:0 auto;align-items: center!important;">
    <div id="timer-label">Remaining time</div>
    <div id="countdown">10:00</div>
    <div id="message"></div>
</div>
<form method="post" action="http://localhost:8080/payment">
    <section style="background-color: lightskyblue">
        <div class="container py-5">
            <div class="row d-flex justify-content-center">
                <div class="col-md-9 col-lg-7 col-xl-5">
                    <div class="card">
                        <div class="card-body">
                            <div class="card-title d-flex justify-content-between mb-0">
                                <p class="mb-0">task price: ${price} $</p>
                            </div>
                        </div>
                        <div class="rounded-bottom" style="background-color: darkcyan">
                            <div class="card-body">
                                <p class="mb-4">Your payment details</p>

                                <div class="form-outline mb-3">
                                    <input name="cardNo"
                                           type="text"
                                           id="cardNumber"
                                           class="form-control"
                                           placeholder="1234567812345678"
                                    />
                                    <label class="form-label" for="cardNumber"
                                    >Card Number</label
                                    >
                                </div>

                                <div class="col-6">
                                    <div class="form-outline">
                                        <input name="cvv"
                                               type="password"
                                               maxlength="6"
                                               minlength="3"
                                               id="cvv"
                                               class="form-control"
                                               placeholder="Cvv2"
                                        />
                                        <label class="form-label" for="cvv">Cvv</label>
                                        <input name="month"
                                               type="text"
                                               id="mm"
                                               class="form-control"
                                               placeholder="MM"
                                        />
                                        <label class="form-label" for="mm">Month</label>
                                        <input name="year"
                                               type="text"
                                               id="yy"
                                               class="form-control"
                                               placeholder="YY"
                                        />
                                        <label class="form-label" for="yy">Year</label>
                                        <input name="password"
                                               type="password"
                                               maxlength="8"
                                               minlength="4"
                                               id="password"
                                               class="form-control"
                                               placeholder="Password"
                                        />
                                        <label class="form-label" for="password"
                                        >Password</label
                                        >
                                    </div>
                                    <div
                                            class="g-recaptcha"
                                            data-sitekey="6Le1ZGUpAAAAAOntvyxRpAlDsGiAT_Jv3YFHLAAK"
                                    ></div>
                                </div>
                                <button
                                        type="submit"
                                        value="submit"
                                        id="submit"
                                        class="btn btn-info btn-block"
                                >
                                    Pay now
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</form>
<script>

    let countdown = document.getElementById('countdown');
    let message = document.getElementById('message');
    let button = document.getElementById('submit');
    let timeleft = 600; // 10 minutes in seconds

    function startCountdown() {
        let timer = setInterval(function () {
            let minutes = Math.floor(timeleft / 60);
            let seconds = timeleft % 60;

            countdown.innerHTML = ('0' + minutes).slice(-2) + ':' + ('0' + seconds).slice(-2);
            timeleft--;

            if (timeleft < 0) {
                clearInterval(timer);
                countdown.innerHTML = '00:00';
                button.disabled = true;
                button.classList.add('disabled');
                message.innerHTML = 'Time is out! Please re-initiate the payment.';
                message.style.backgroundColor = "red";
            }
        }, 1000);
    }

    // Call startCountdown function when the page loads
    window.onload = function () {
        startCountdown();
    };
</script>
<%--<script>--%>
<%--    document.getElementById("submit").addEventListener("click", function () {--%>
<%--        const cardNumber = document.getElementById("cardNumber").value;--%>
<%--        const cvv = document.getElementById("cvv").value;--%>
<%--        const month = document.getElementById("mm").value;--%>
<%--        const year = document.getElementById("yy").value;--%>
<%--        const password = document.getElementById("password").value;--%>

<%--        const url = "http://localhost:8089/user/payment";--%>
<%--        const data = {--%>
<%--            cardNumber,--%>
<%--            cvv,--%>
<%--            month,--%>
<%--            year,--%>
<%--            password,--%>
<%--        };--%>

<%--        fetch(url, {--%>
<%--            method: "POST",--%>
<%--            headers: {--%>
<%--                "Content-Type": "application/json",--%>
<%--            },--%>
<%--            body: JSON.stringify(data),--%>
<%--        })--%>
<%--            .then((response) => {--%>
<%--                if (response.ok) {--%>
<%--                    console.log("success");--%>
<%--                    console.log(response);--%>
<%--                } else {--%>
<%--                    console.log("Request failed: " + response.status);--%>
<%--                }--%>
<%--            })--%>
<%--            .catch((error) => {--%>
<%--                console.log("An error occurred: " + error);--%>
<%--            });--%>
<%--    });--%>
<%--</script>--%>
</body>
</html>