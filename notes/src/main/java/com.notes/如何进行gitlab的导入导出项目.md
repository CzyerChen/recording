> gitlab项目如何导入导出?
> gitlab api的一些操作

#### 整个项目的导入导出
- [官方文档](https://docs.gitlab.com/ce/user/project/settings/import_export.html)
- 官方文档详细描述了如何导入导出gitlab项目，包含所有配置，部分进度追踪不包含
- 注意safari打开并下载时会导致下载包缺失.gz的压缩，属于浏览器的bug,因而可使用其他浏览器

### 通过gitlab API可以获得什么？
- 1.创建token，给予api权限，页面操作即可
- 2.获取GroupId：登录Gitlab后访问“local host+/api/v4/groups?private_token=Private token ”
```json
[
    {
        "id": 8,
        "web_url": "http://ip/groups/tech",
        "name": "tech",
        "path": "tech",
        "description": "Research and develop center",
        "visibility": "private",
        "lfs_enabled": true,
        "avatar_url": null,
        "request_access_enabled": true,
        "full_name": "tech",
        "full_path": "tech",
        "parent_id": null
    }
]

```
- 3.获取ProjectId：访问“local host+/api/v4/groups/GroupId/projects/?private_token=Private token ”
```json
[
    {
        "id": 36,
        "description": "",
        "name": "xxxx",
        "name_with_namespace": "tech/ xxxx",
        "path": "xxxx",
        "path_with_namespace": "tech/xxxx",
        "created_at": "2019-08-30T17:59:15.042Z",
        "default_branch": "master",
        "tag_list": [],
        "ssh_url_to_repo": "git@ip:tech/xxxx.git",
        "http_url_to_repo": "http://ip:tech/xxxx.git",
        "web_url": "http://ip/tech/xxxx",
        "readme_url": "http://ip/tech/xxxx/blob/master/README.md",
        "avatar_url": null,
        "star_count": 0,
        "forks_count": 0,
        "last_activity_at": "2019-10-21T09:13:45.227Z",
        "namespace": {
            "id": 8,
            "name": "tech",
            "path": "tech",
            "kind": "group",
            "full_path": "tech",
            "parent_id": null,
            "avatar_url": null,
            "web_url": "http://ip/groups/tech"
        },
        "_links": {
            "self": "",
            "issues": "",
            "merge_requests": "",
            "repo_branches": "",
            "labels": "",
            "events": "",
            "members": ""
        },
        "archived": false,
        "visibility": "private",
        "resolve_outdated_diff_discussions": false,
        "container_registry_enabled": true,
        "issues_enabled": true,
        "merge_requests_enabled": true,
        "wiki_enabled": true,
        "jobs_enabled": true,
        "snippets_enabled": true,
        "shared_runners_enabled": true,
        "lfs_enabled": true,
        "creator_id": 23,
        "import_status": "none",
        "open_issues_count": 18,
        "public_jobs": true,
        "ci_config_path": null,
        "shared_with_groups": [],
        "only_allow_merge_if_pipeline_succeeds": false,
        "request_access_enabled": false,
        "only_allow_merge_if_all_discussions_are_resolved": false,
        "printing_merge_request_link_enabled": true,
        "merge_method": "merge",
        "external_authorization_classification_label": null
    }
]
```

- 4获取Issue相关json字符串,通过访问“local host+/api/v3/projects/ProjectId/issues?private_token=Private token”
```json
[
    {
        "id": 121,
        "iid": 120,
        "project_id": 36,
        "title": "初始化日期不匹配",
        "description": "",
        "state": "opened",
        "created_at": "2019-10-18T18:55:47.824Z",
        "updated_at": "2019-10-18T19:29:12.151Z",
        "closed_at": null,
        "closed_by": null,
        "labels": [
            "Bug"
        ],
        "milestone": null,
        "assignees": [
            {
                "id": 14,
                "name": "You",
                "username": "you",
                "state": "active",
                "avatar_url": "",
                "web_url": ""
            }
        ],
        "author": {
            "id": 14,
            "name": "You",
            "username": "you",
            "state": "active",
            "avatar_url": "",
            "web_url": ""
        },
        "assignee": {
            "id": 14,
            "name": "You",
            "username": "you",
            "state": "active",
            "avatar_url": "",
            "web_url": ""
        },
        "user_notes_count": 1,
        "merge_requests_count": 0,
        "upvotes": 0,
        "downvotes": 0,
        "due_date": "2019-10-18",
        "confidential": false,
        "discussion_locked": null,
        "web_url": "http://ip/tech/xxxx/issues/120",
        "time_stats": {
            "time_estimate": 0,
            "total_time_spent": 0,
            "human_time_estimate": null,
            "human_total_time_spent": null
        }
    }
]

```
- [gitlab wiki](https://wiki.archlinux.org/index.php/Gitlab_(简体中文))

- [导出issue编程csv](https://www.jianshu.com/p/1e575ce21bac)

