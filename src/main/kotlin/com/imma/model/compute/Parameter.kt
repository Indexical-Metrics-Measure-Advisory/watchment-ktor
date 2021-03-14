package com.imma.model.compute

enum class ParameterKind(val kind: String) {
    topic("topic"),
    constant("constant"),
    computed("computed");
}

open class Parameter(var kind: ParameterKind)

data class TopicFactorParameter(
    var topicId: String,
    var factorId: String,
) : Parameter(kind = ParameterKind.topic)

data class ConstantParameter(
    var value: String = ""
) : Parameter(kind = ParameterKind.constant)

enum class ParameterComputeType(val type: String) {
    none("none"),
    add("add"),
    subtract("subtract"),
    multiply("multiply"),
    divide("divide"),
    modulus("modulus"),
    `year-of`("year-of"),
    `half-year-of`("half-year-of"),
    `quarter-of`("quarter-of"),
    `month-of`("month-of"),
    `week-of-year`("week-of-year"),
    `week-of-month`("week-of-month"),
    `day-of-month`("day-of-month"),
    `day-of-week`("day-of-week");
}

data class ComputedParameter(
    var type: ParameterComputeType = ParameterComputeType.none,
    var parameters: List<Parameter>
) : Parameter(kind = ParameterKind.computed)