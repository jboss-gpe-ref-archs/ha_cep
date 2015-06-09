package org.jboss.ddoyle.brms.cep.ha.cdi;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface Eager
{
}