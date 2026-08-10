package com.studio.api.portfolio.entity;

/**
 * How heavy a case-study block is, so the detail page can render measured
 * problem-solving work and routine feature work as two separate sections
 * instead of giving both the same weight.
 */
public enum ProjectProblemKind {

    /** A diagnosed problem with an approach and measured results. */
    PROBLEM,

    /** Routine feature work — described, but without a before/after story. */
    FEATURE
}
