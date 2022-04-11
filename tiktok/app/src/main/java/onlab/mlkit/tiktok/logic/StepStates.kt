package onlab.mlkit.tiktok.logic

import ru.nsk.kstatemachine.DefaultFinalState
import ru.nsk.kstatemachine.DefaultState
import ru.nsk.kstatemachine.Event


sealed class Events {
    object InitToFirst : Event
    object FirstToSecond : Event
    object SecondToThird : Event
    object ThirdToFourth : Event
    object FourthToFifth : Event
    object FifthToSixth : Event
    object SixthToFinal : Event
    object SwitchState : Event
}

sealed class States {
    object InitStep : DefaultState()
    object FirstStep : DefaultState()
    object SecondStep : DefaultState()
    object ThirdStep : DefaultState()
    object FourthStep : DefaultState()
    object FifthStep : DefaultState()
    object SixthStep : DefaultState()
    object FinalStep : DefaultFinalState()
}

