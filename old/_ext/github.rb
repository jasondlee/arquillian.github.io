# -*- encoding : utf-8 -*-
require 'parallel'

module Awestruct::Extensions::Github
  DEFAULT_BASE_URL = 'https://api.github.com'

  class Project
    def initialize(pipeline, *args)
      pipeline.extension ReleaseNotes.new *args
    end
  end

  class ReleaseNotes
    ISSUES_TEMPLATE = '/repos/%s/issues?milestone=%s&state=closed'
    DURATION_1_DAY = 60 * 60 * 24

    def initialize(project_key, prefix_version = nil, base_url = DEFAULT_BASE_URL)
      @project_key = project_key
      @prefix_version = prefix_version
      @base_url = base_url
    end

    def execute(site)
      site.release_notes ||= {}
      # just in case we need other data, we'll just grab the versions from the project resource

      more_pages = true
      page = 1
      while more_pages do

        milestones_template = "/repos/%s/milestones?state=closed&page=#{page}"
        milestones_url = @base_url + (milestones_template % @project_key)
        milestones_data = RestClient.get milestones_url, :accept => 'application/json',
            :cache_key => "github/milestones_project-#{@project_key}-#{page}.json", :cache_expiry_age => DURATION_1_DAY

        if milestones_data.content.size == 0
          more_pages = false
          break
        end
        page += 1

        Parallel.map(milestones_data.content, progress: "Fetching milestones of [#{milestones_url}] ") { |m|
          release_key = m['title']
          release_key = "#{@prefix_version}_#{release_key}" unless @prefix_version.nil?
          issues_url = @base_url + (ISSUES_TEMPLATE % [@project_key, m['number']])
          issues_data = RestClient.get issues_url, :accept => 'application/json', :cache_key => "github/issues-#{release_key}.json"

          release_notes = OpenStruct.new({
            :id => m['title'],
            :comment => m['description'],
            :key => release_key,
            :html_url => issues_url,
            :resolved_issues => {}
          })
          issues_data.content.each do |e|
            type = 'Other'
            type = e['labels'].first()['name'] if e['labels'] and e['labels'].first
            release_notes.resolved_issues[type] = [] if !release_notes.resolved_issues.has_key? type
            release_notes.resolved_issues[type] << "<a href='#{e['html_url']}'>##{e['number']} #{e['title']}</a>"
          end

          [release_key, release_notes]
        }.each { |release_key, release_notes|
          site.release_notes[release_key] = release_notes
        }
      end
    end
  end

end
