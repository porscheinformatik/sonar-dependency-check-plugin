class DependencycheckController < ApplicationController

    SECTION = Navigation::SECTION_RESOURCE

    def index
        init_resource_for_user_role
        @entries = load_dependencies
        @licenses = load_licenses
    end

    def export_csv
        init_resource_for_user_role
        @entries = load_dependencies
        @filename = 'filename.csv'
        render :template => 'dependencycheck/export_csv', :layout => false
    end

    protected

    def load_dependencies
        
        project_sid = @snapshot.id
        dep_usage = []
        if Api::Utils.java_facade.getSettings().getBoolean('dependencycheck.scope.compile')
            dep_usage.push('compile')
        end
        if Api::Utils.java_facade.getSettings().getBoolean('dependencycheck.scope.provided')
            dep_usage.push('provided')
        end
        if Api::Utils.java_facade.getSettings().getBoolean('dependencycheck.scope.runtime')
            dep_usage.push('runtime')
        end
        if Api::Utils.java_facade.getSettings().getBoolean('dependencycheck.scope.test')
            dep_usage.push('test')
        end

        projects_sids = [project_sid]
        if @snapshot.qualifier == 'TRK'
            projects_sids += Snapshot.find(:all, :select => 'id', :conditions => ["scope = 'PRJ' AND root_snapshot_id=?", project_sid])
        end

        dependencies = Dependency.find(:all,
            :include => ['to','to_snapshot'],
            :conditions => ["project_snapshot_id in (?) and to_scope = 'PRJ' and dep_usage in (?)", projects_sids, dep_usage],
            :order => "projects.kee")

        depMeasure = @snapshot.measure('dependencycheck.dependency').data
        checkedDependencies = depMeasure.split(';')
        entries = []
        dependencies.each do |dep|
            license = ''
            status = ''
            checkedDependencies.each do |checkedDep|
                d = checkedDep.split('~')
                if dep.to.key == d[0]
                    license = d[1]
                    status = d[2]
                    break 
                end
            end
            if status != ''
                entries.push([dep.to.name, dep.dep_usage, dep.to_snapshot.version, license, status])
            end
        end

        entries.uniq
    end

    def load_licenses
        licMeasure = @snapshot.measure('dependencycheck.license').data
        licMeasure.split(';')
    end

    def load_data
        
        load_dependencies
        load_licenses
    end
end