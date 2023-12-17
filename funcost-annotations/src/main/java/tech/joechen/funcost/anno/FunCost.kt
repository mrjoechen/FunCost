package tech.joechen.funcost.anno

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class FunCost(val showThread: Boolean = false)
