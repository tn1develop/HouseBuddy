# HouseBuddy

**Find out the real cost of buying a home in Italy.**

HouseBuddy is a house price calculator built around how the Italian property market actually works. When it comes to buying a house, there are many hidden costs that one might not be aware of: notary fees, registration taxes, agency commissions, mortgage setup costs, and insurance are just some of the things a buyer should worry about. This app estimates those hidden costs and gives you a clear picture of what you will need upfront and month by month.

Available on **Android** and **iOS**, from a single shared codebase.

---

## Why HouseBuddy?

Buying a house in Italy involves a maze of fees that rarely appear in the advertised price. HouseBuddy is designed for anyone navigating that process — first-time buyers, couples splitting costs, or expats trying to understand *mutuo*, *caparra*, and *rendita catastale*.

Adjust a few inputs and instantly see:

- **Upfront liquidity** — how much cash you need at or before closing
- **Estimated monthly mortgage payment** — based on your loan amount, rate, and term
- **Full cost breakdown** — caparra, down payment, agency fees (with VAT), notary and registration taxes, mortgage fees, and insurance
- **Per-person totals** — when buying with a partner or co-buyers

Estimates are indicative, not legal or financial advice — but they help you plan with far more confidence than the listing price alone.

---

## Features

### Cost calculator

The main screen lets you model a purchase step by step:

| Input | What it drives |
|-------|----------------|
| House price | Base purchase amount |
| Mortgage request (%) | Loan size vs. down payment |
| Deposit (*caparra*) | Amount already paid to reserve the property |
| Agency commission | Percentage or fixed fee (with 22% VAT) |
| Cadastral income (*rendita catastale*) | Registration tax (*imposta di registro*) |
| Mortgage rate & term | Monthly installment estimate |
| Number of buyers | Per-person cost split |

Tap the info icon on the results to see every line item: notary purchase costs, mortgage origination fees, fire and life insurance, and more.

### Settings

Fine-tune assumptions that affect the calculation:

- **Green mortgage** (*mutuo green*) for energy-class A/B properties — adjusts default rates
- Agency fee mode (percentage vs. fixed amount)
- Mortgage duration, interest rate, and buyer count

All preferences are **persisted locally** so your scenario survives app restarts.

### Historical EURIBOR trends

A dedicated screen fetches **EURIBOR** historical data from the [European Central Bank](https://data-api.ecb.europa.eu/) SDMX API and displays it as a monthly line chart — useful context when estimating where mortgage rates might sit.

### Carpe Diem — rent vs. buy

Wondering whether to keep renting or buy now? The **Carpe Diem** scenario planner compares total cost over up to 30 years:

- Enter your current rent, savings, and annual savings rate
- The app finds the highest mortgage percentage your liquidity can support each year
- A mirrored bar chart shows **total cost of ownership + cumulative rent** year by year

A practical “should I wait or act?” view, grounded in Italian purchase mechanics, will tell estimate for you when the best time for buying will be.

### Bluetooth transfer

Share your saved calculation settings with another device over **Bluetooth** — handy when comparing scenarios with a partner on their phone.

---

## Tech stack

| Layer | Technology |
|-------|------------|
| Language | [Kotlin](https://kotlinlang.org/) 2.3 |
| Multiplatform | [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) |
| UI | [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/) + Material 3 |
| Navigation | [Navigation Compose](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-navigation-routing.html) |
| Networking | [Ktor](https://ktor.io/) Client (OkHttp on Android, Darwin on iOS) |
| Persistence | [multiplatform-settings](https://github.com/russhwolf/multiplatform-settings) |
| Concurrency | [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) |
| Targets | Android (min SDK 24) · iOS (arm64 + Simulator) |

---

## Architecture

The project follows **Clean Architecture** with a lightweight **MVI** (Model–View–Intent) presentation layer. Business rules live in pure Kotlin use cases; the UI stays declarative and platform-agnostic.


### Layers

| Package | Responsibility |
|---------|----------------|
| `domain/` | Pure business logic — `CalculateHousePriceUseCase`, `SimulateScenariosUseCase`, `FetchEuriborMonthlyRatesUseCase`, and domain models |
| `data/` | `EcbExchangeRateRepository`, HTTP client setup, SDMX XML parsing, local state persistence |
| `presentation/mvi/` | ViewModels, ViewState, and Events — unidirectional data flow |
| `ui/` | Compose screens, reusable components, theme, and navigation |
| `transfer/` | Platform-specific Bluetooth via `expect` / `actual` |

### Patterns in practice

- **Use cases** encapsulate calculation and data-fetching logic, keeping ViewModels thin
- **Repository** abstracts the ECB API behind a suspend function returning domain models
- **MVI**: UI dispatches `HousePriceEvent` / `ExchangeRateEvent` → ViewModel updates `ViewState` → UI recomposes
- **Derived state**: `HousePriceViewModel.result` and `scenarioResult` are computed from current state via use cases (no redundant caching)
- **expect / actual** for platform engines (HTTP, Bluetooth, charts, settings backends) while sharing ~95% of the app in `commonMain`


## Getting started

### Prerequisites

- **Android**: Android Studio with KMP support
- **iOS**: Xcode (to build and run the iOS app)
- JDK 11+

### Run the apps

- **Android**: `./gradlew :androidApp:assembleDebug` — or use the IDE run configuration
- **iOS**: Open [`iosApp/`](./iosApp) in Xcode and run on a simulator or device

---

## What gets calculated?

HouseBuddy models costs typical of an Italian residential purchase, including:

**At closing (notary — purchase)**
- Registration tax (*imposta di registro*)
- Mortgage and cadastral taxes
- Archive fee, notary fees, CNN contributions, land registry searches

**Mortgage setup**
- Application fee (*istruttoria*)
- Substitute tax (*imposta sostitutiva*)
- Appraisal (*perizia*)
- Mandatory fire insurance and life insurance
- Mortgage notary fees

**Agency & down payment**
- Agency commission + VAT
- Deposit (*caparra*) and remaining down payment

Default values reflect common Italian assumptions; everything is adjustable in Settings.

---

## Disclaimer

HouseBuddy provides **rough estimates** for planning purposes. Tax rates, notary tariffs, and bank fees vary by region, property type, buyer status (first home, etc.), and individual agreements. Always confirm figures with a notary, accountant, or financial advisor before making decisions.

---

## Learn more

- [Kotlin Multiplatform documentation](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)
