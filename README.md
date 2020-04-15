# Paysera
Currency Exchanger
Your homework task will be to create an application for exchanging currencies. Take a look how the app should look like at https://scene.zeplin.io/project/5da4169f760d2a2fb4b2ead9. Screen images are to get better understanding how everything should work and you don't have to make it look exactly the same. However, if you wish to use resources (images, colors) from the design please email us your Zeplin email.

The user has a multi-currency account with starting balance of 1000 Euros (EUR). He can convert any currency to any if the rate is provided by the API but the balance can't fall below zero. Create an input where the user will enter amount, picker for currency being sold and a picker for currency being bought.

For example, user inputs 100.00, picks Euros to sell and Dollars to buy. User then clicks Submit button, a message is shown You have converted 100.00 EUR to 110.30 USD and now the balance is 900.00 Euros and 110.30 US Dollars.

Also, there may be a commission fee for the currency exchange operation. The first five currency exchanges are free of charge but afterwards they're charged 0.7% of the currency being traded. The commission fee should be displayed in the message that appears after the conversion. For example:

You have converted 100.00 EUR to 110.00 USD. Commission Fee - 0.70 EUR.
The commission fee should be deducted from each currency balance separately.

Currency Exchange Rate API
The API is public and no authentication is required
Currency exchange rates should be synchronized every 5 seconds
URI: https://api.exchangeratesapi.io/latest

Response example:
{
  "rates": {
    "CAD": 1.5521,
    "HKD": 8.5095,
    "ISK": 154.0,
    "PHP": 56.125,
    "DKK": 7.4606,
    "HUF": 355.65,
    "CZK": 27.299,
    "AUD": 1.8209,
    "RON": 4.8375,
    "SEK": 11.0158,
    "IDR": 17716.88,
    "INR": 82.8695,
    "BRL": 5.5905,
    "RUB": 86.3819,
    "HRK": 7.614,
    "JPY": 119.36,
    "THB": 35.769,
    "CHF": 1.0581,
    "SGD": 1.5762,
    "PLN": 4.5306,
    "BGN": 1.9558,
    "TRY": 7.0935,
    "CNY": 7.7894,
    "NOK": 11.6558,
    "NZD": 1.8548,
    "ZAR": 19.3415,
    "USD": 1.0977,
    "MXN": 25.8329,
    "ILS": 3.9413,
    "GBP": 0.89743,
    "KRW": 1346.31,
    "MYR": 4.7619
  },
  "base": "EUR",
  "date": "2020-03-27"
}

# Requirements
the task must be done in Kotlin
there are no strict restrictions on time
third-party libraries, tools, frameworks can be used
the system should be maintainable & expandable:
a clear relationship between the parts of the code
the code is understandable, simple, readable
the addition of a new functionality or an existing change should not require rewriting the entire system
adding new currency should be easy
provide for the possibility of expanding the calculation of a more flexible commission. It is possible to come up with various new rules, for example - every tenth conversion is free, conversion of up to 200 Euros is free of charge etc.
balance must be visible somewhere in the UI
balance can't be negative after the conversion
the code should conform to the selected code standards (e.g. google java style guide, square java style guide).
Evaluation
all requirements are met
code quality
knowledge of Android ecosystem - libraries, frameworks
knowledge of best practices, design & architecture patterns
(insanely) great UI/UX is an advantage

# Design Pattern

 **MVVM Kotlin**

 1. **Increases the "Blendability" of your views** (ability to use Expression Blend to design views). This enables a separation of responsibilities on teams that are lucky enough to have a designer and a programmer... each can work independent of the other.
 2. **"Lookless" view logic.** Views are agnostic from the code that runs behind them, enabling the same view logic to be reused across multiple views or have a view easily retooled or replaced. Seperates concerns between "behavior" and "style".
 3. **No duplicated code to update views.** In code-behind you will see a lot of calls to "myLabel.Text = newValue" sprinkled everywhere. With MVVM you can be assured the view is updated appropriately just by setting the underlying property and all view side-effects thereof.
 4. **Testability.** Since your logic is completely agnostic of your view (no "myLabel.Text" references), unit testing is made easy. You can test the behavior of a ViewModel without involving its view. This also enabled test-driven development of view behavior, which is almost impossible using code-behind.


# Setup
 * Create new empty project.
 * Add required dependencies for this project that shown below.
 * Used Dagger2 for injection.
 * Used Rxjava for Observable, AndroidSchedulers, Schedulers.
 * Used Room database to store data from the response. 
 * When connection is gone load data from database.
 * Used LiveData for displaying movie list in the RecyclerView.
 * From Recycler view, used DataBinding to minimize coding.

# Major libraries
 * Kotlin
 * Android Architecture Components (ViewModel, LiveData, Room, Data Binding)
 * Retrofit
 * Dagger2
 * RxJava
 

