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
package org.apache.maven.lifecycle.internal;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.LifecycleNotFoundException;
import org.apache.maven.lifecycle.LifecyclePhaseNotFoundException;
import org.apache.maven.plugin.*;
import org.apache.maven.plugin.prefix.NoPluginFoundForPrefixException;
import org.apache.maven.plugin.version.PluginVersionResolutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Kristian Rosenvold
 *         This class is not part of any public api and can be changed or deleted without prior notice.
 */
@Component(role = BuildListCalculator.class)
public class BuildListCalculator
{
    @Requirement
    private LifecycleTaskSegmentCalculator lifeCycleTaskSegmentCalculator;

    @SuppressWarnings({"UnusedDeclaration"})
    public BuildListCalculator()
    {
    }

    public BuildListCalculator( LifecycleTaskSegmentCalculator lifeCycleTaskSegmentCalculator )
    {
        this.lifeCycleTaskSegmentCalculator = lifeCycleTaskSegmentCalculator;
    }

    public List<TaskSegment> calculateTaskSegments( MavenSession session )
        throws PluginNotFoundException, PluginResolutionException, PluginDescriptorParsingException,
        MojoNotFoundException, NoPluginFoundForPrefixException, InvalidPluginDescriptorException,
        PluginVersionResolutionException, LifecyclePhaseNotFoundException, LifecycleNotFoundException
    {

        MavenProject rootProject = session.getTopLevelProject();

        List<String> tasks = session.getGoals();

        if ( tasks == null || tasks.isEmpty() )
        {
            if ( !StringUtils.isEmpty( rootProject.getDefaultGoal() ) )
            {
                tasks = Arrays.asList( StringUtils.split( rootProject.getDefaultGoal() ) );
            }
        }

        return lifeCycleTaskSegmentCalculator.calculateTaskSegments( session, tasks );
    }

    public ProjectBuildList calculateProjectBuilds( MavenSession session, List<TaskSegment> taskSegments )
    {
        List<ProjectSegment> projectBuilds = new ArrayList<ProjectSegment>();

        MavenProject rootProject = session.getTopLevelProject();

        for ( TaskSegment taskSegment : taskSegments )
        {
            List<MavenProject> projects;

            if ( taskSegment.isAggregating() )
            {
                projects = Collections.singletonList( rootProject );
            }
            else
            {
                projects = session.getProjects();
            }
            for ( MavenProject project : projects )
            {
                BuilderCommon.attachToThread( project ); // Not totally sure if this is needed for anything
                MavenSession copiedSession = session.clone();
                copiedSession.setCurrentProject( project );
                projectBuilds.add( new ProjectSegment( project, taskSegment, copiedSession ) );
            }
        }
        return new ProjectBuildList( projectBuilds );
    }
}
