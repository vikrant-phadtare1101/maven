/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.maven.lifecycle.internal.stub;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.LifecycleNotFoundException;
import org.apache.maven.lifecycle.LifecyclePhaseNotFoundException;
import org.apache.maven.lifecycle.MavenExecutionPlan;
import org.apache.maven.lifecycle.internal.ExecutionPlanItem;
import org.apache.maven.lifecycle.internal.LifecycleExecutionPlanCalculator;
import org.apache.maven.lifecycle.internal.ProjectBuildList;
import org.apache.maven.lifecycle.internal.ProjectSegment;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoNotFoundException;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.plugin.PluginNotFoundException;
import org.apache.maven.plugin.PluginResolutionException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.prefix.NoPluginFoundForPrefixException;
import org.apache.maven.plugin.version.PluginVersionResolutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:kristian@zenior.no">Kristian Rosenvold</a>
 */
public class LifecycleExecutionPlanCalculatorStub
    implements LifecycleExecutionPlanCalculator
{
    public final static MojoDescriptor CLEAN = createMojoDescriptor( "clean" );

    public final static MojoDescriptor VALIDATE = createMojoDescriptor( "validate" );

    public final static MojoDescriptor TEST_COMPILE = createMojoDescriptor( "test-compile" );

    public final static MojoDescriptor PROCESS_TEST_RESOURCES = createMojoDescriptor( "process-test-resources" );

    public final static MojoDescriptor PROCESS_RESOURCES = createMojoDescriptor( "process-resources" );

    public final static MojoDescriptor COMPILE = createMojoDescriptor( "compile" );

    public final static MojoDescriptor TEST = createMojoDescriptor( "test" );

    public final static MojoDescriptor PACKAGE = createMojoDescriptor( "package" );

    public final static MojoDescriptor INSTALL = createMojoDescriptor( "install" );

    public int getNumberOfExceutions( ProjectBuildList projectBuildList )
        throws InvalidPluginDescriptorException, PluginVersionResolutionException, PluginDescriptorParsingException,
        NoPluginFoundForPrefixException, MojoNotFoundException, PluginNotFoundException, PluginResolutionException,
        LifecyclePhaseNotFoundException, LifecycleNotFoundException
    {
        int result = 0;
        for ( ProjectSegment projectBuild : projectBuildList )
        {
            MavenExecutionPlan plan = calculateExecutionPlan( projectBuild.getSession(), projectBuild.getProject(),
                                                              projectBuild.getTaskSegment().getTasks() );
            result += plan.size();
        }
        return result;
    }

    public MavenExecutionPlan calculateExecutionPlan( MavenSession session, MavenProject project, List<Object> tasks )
        throws PluginNotFoundException, PluginResolutionException, LifecyclePhaseNotFoundException,
        PluginDescriptorParsingException, MojoNotFoundException, InvalidPluginDescriptorException,
        NoPluginFoundForPrefixException, LifecycleNotFoundException, PluginVersionResolutionException
    {
        if ( project.equals( ProjectDependencyGraphStub.A ) )
        {
            return getProjectAExceutionPlan();
        }
        if ( project.equals( ProjectDependencyGraphStub.B ) )
        {
            return getProjectBExecutionPlan();
        }
        // The remaining are basically "for future expansion"
        List<MojoExecution> me = new ArrayList<MojoExecution>();
        me.add( createMojoExecution( new Plugin(), "resources", "default-resources", PROCESS_RESOURCES ) );
        me.add( createMojoExecution( new Plugin(), "compile", "default-compile", COMPILE ) );
        return new MavenExecutionPlan( getScopes(), getScopes(),
                                       DefaultLifecyclesStub.createDefaultLifeCycles().createExecutionPlanItem( project,
                                                                                                                me ) );
    }

    public static MavenExecutionPlan getProjectAExceutionPlan()
        throws PluginNotFoundException, PluginResolutionException, LifecyclePhaseNotFoundException,
        PluginDescriptorParsingException, MojoNotFoundException, InvalidPluginDescriptorException,
        NoPluginFoundForPrefixException, LifecycleNotFoundException, PluginVersionResolutionException
    {
        List<MojoExecution> me = new ArrayList<MojoExecution>();
        me.add( createMojoExecution( new Plugin(), "enforce", "enforce-versions", VALIDATE ) );
        me.add( createMojoExecution( new Plugin(), "resources", "default-resources", PROCESS_RESOURCES ) );
        me.add( createMojoExecution( new Plugin(), "compile", "default-compile", COMPILE ) );
        me.add( createMojoExecution( new Plugin(), "testResources", "default-testResources", PROCESS_TEST_RESOURCES ) );
        me.add( createMojoExecution( new Plugin(), "testCompile", "default-testCompile", TEST_COMPILE ) );
        me.add( createMojoExecution( new Plugin(), "test", "default-test", TEST ) );
        me.add( createMojoExecution( new Plugin(), "war", "default-war", PACKAGE ) );
        me.add( createMojoExecution( new Plugin(), "install", "default-install", INSTALL ) );
        final List<ExecutionPlanItem> executionPlanItem =
            DefaultLifecyclesStub.createDefaultLifeCycles().createExecutionPlanItem(
                ProjectDependencyGraphStub.A.getExecutionProject(), me );
        return new MavenExecutionPlan( getScopes(), getScopes(), executionPlanItem );
    }

    public static MavenExecutionPlan getProjectBExecutionPlan()
        throws PluginNotFoundException, PluginResolutionException, LifecyclePhaseNotFoundException,
        PluginDescriptorParsingException, MojoNotFoundException, InvalidPluginDescriptorException,
        NoPluginFoundForPrefixException, LifecycleNotFoundException, PluginVersionResolutionException
    {
        List<MojoExecution> me = new ArrayList<MojoExecution>();
        me.add( createMojoExecution( new Plugin(), "enforce", "enforce-versions", VALIDATE ) );
        me.add( createMojoExecution( new Plugin(), "resources", "default-resources", PROCESS_RESOURCES ) );
        me.add( createMojoExecution( new Plugin(), "compile", "default-compile", COMPILE ) );
        me.add( createMojoExecution( new Plugin(), "testResources", "default-testResources", PROCESS_TEST_RESOURCES ) );
        me.add( createMojoExecution( new Plugin(), "testCompile", "default-testCompile", TEST_COMPILE ) );
        me.add( createMojoExecution( new Plugin(), "test", "default-test", TEST ) );
        final List<ExecutionPlanItem> planItem =
            DefaultLifecyclesStub.createDefaultLifeCycles().createExecutionPlanItem(
                ProjectDependencyGraphStub.B.getExecutionProject(), me );
        return new MavenExecutionPlan( getScopes(), getScopes(), planItem );
    }

    private static MojoExecution createMojoExecution( Plugin plugin, String goal, String executionId,
                                                      MojoDescriptor mojoDescriptor )
    {
        MojoExecution result = new MojoExecution( plugin, goal, executionId );
        result.setConfiguration( new Xpp3Dom( executionId + "-" + goal ) );
        result.setMojoDescriptor( mojoDescriptor );
        return result;

    }

    public static MojoDescriptor createMojoDescriptor( String phaseName )
    {
        final MojoDescriptor mojoDescriptor = new MojoDescriptor();
        mojoDescriptor.setPhase( phaseName );
        final PluginDescriptor descriptor = new PluginDescriptor();
        descriptor.setArtifactId( "artifact." + phaseName );
        mojoDescriptor.setPluginDescriptor( descriptor );
        return mojoDescriptor;
    }


    public static Set<String> getScopes()
    {
        return new HashSet<String>( Arrays.asList( "compile" ) );
    }

}
