package machine
import java.util.Scanner
fun main() {
    val scanner = Scanner(System.`in`)
    val coffeeMachine = CoffeeMachine()

    while (scanner.hasNext()) {
        if (!coffeeMachine.perform(scanner.next())){
            break
        }
    }
}

class CoffeeMachine {
    enum class Action {
        BUY, FILL, TAKE, REMAINING, EXIT;
    }

    enum class State {
        CHOOSING_ACTION,
        CHOOSING_COFFEE_TYPE,
        FILLING,
    }

    enum class CoffeeType(val type: Int, val water: Int, val milk: Int, val beans: Int, val price: Int) {
        ESPRESSO(1, 250, 0, 16, 4),
        LATTE(2, 350, 75, 20, 7),
        CAPPUCCINO(3, 200, 100, 12, 6),
        NULL(0,0,0,0, 0);

        companion object {
            fun getTypeByValue(value: Int): CoffeeType{
                for (enum in values()){
                    if (enum.type == value) return enum
                }
                return NULL
            }
        }
    }

    private var state : State = State.CHOOSING_ACTION
    private var action : Action = Action.BUY;
    private var money : Int = 550
    private var water : Int = 400
    private var milk : Int = 540
    private var coffeeBeans : Int = 120
    private var disposableCups : Int = 9
    private var fillStep = 1

    init {
        showStartMessage()
    }

    fun perform(input: String) : Boolean{
        if (state == State.CHOOSING_ACTION) {
            action = Action.valueOf(input.toUpperCase())
        }

        when(action) {
            Action.TAKE -> {
                println("I gave you $money")
                money = 0
                resetState()
                return true
            }
            Action.REMAINING -> {
                showMachineState()
                resetState()
                return true
            }
            Action.EXIT -> return false
        }

        when(state) {
            State.FILLING -> {
                fill(input)
                return true
            }
            State.CHOOSING_COFFEE_TYPE -> {
                if (input == "back") {
                    resetState();
                    return true
                }
                val type = CoffeeType.getTypeByValue(input.toInt())
                makeCoffee(type)
                resetState()
                return true
            }
        }

        setState(action)
        return true
    }

    private fun setState(action: Action) {
        when(action) {
            Action.BUY -> {
                state = State.CHOOSING_COFFEE_TYPE
                println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ")
            }
            Action.FILL -> {
                state = State.FILLING
                println("Write how many ml of water do you want to add: ")
            }
        }
    }

    private fun fill(items: String) {
        when(fillStep){
            1 -> {
                water += items.toInt()
                println("Write how many ml of milk do you want to add: ")
            }
            2 -> {
                milk += items.toInt()
                println("Write how many grams of coffee beans do you want to add: ")
            }
            3 -> {
                coffeeBeans += items.toInt()
                println("Write how many disposable cups of coffee do you want to add: ")
            }
            4 -> {
                disposableCups += items.toInt()
                fillStep = 1
                resetState()
                return
            }
        }
        fillStep++
    }

    private fun makeCoffee(type: CoffeeType) {
        if (canMakeCoffee(type)){
            println("I have enough resources, making you a coffee!")
            milk -= type.milk
            water -= type.water
            coffeeBeans -= type.beans
            disposableCups--
            money += type.price
        }

    }

    private fun canMakeCoffee(type: CoffeeType): Boolean {
       when{
            water < type.water -> {
                showResourceNotEnoughMessage("water")
                return false
            }
            milk < type.milk -> {
                showResourceNotEnoughMessage("milk")
                return false
            }
            coffeeBeans < type.beans -> {
                showResourceNotEnoughMessage("coffee beans")
                return false
            }
            disposableCups == 0 -> {
                showResourceNotEnoughMessage("disposable cups")
                return false
            }
            else -> return  true
        }
    }

    private fun showResourceNotEnoughMessage(resource: String) {
        println("Sorry, not enough ${resource}!")
    }

    private fun showMachineState () {
        println("The coffee machine has:")
        println("$water of water")
        println("$milk of milk")
        println("$coffeeBeans of coffee beans")
        println("$disposableCups of disposable cups")
        println("\$$money of money")
    }

    private fun showStartMessage() {
        println("Write action (buy, fill, take, remaining, exit): ")
    }

    private fun resetState() {
        state = State.CHOOSING_ACTION
        showStartMessage()
    }
}

