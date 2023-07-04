# go-struct-to-struct

IntelliJ plugin that Automatically generate two struct transformations through function declarations

### Usage

- define func

```
func transform(d *data.SiteResult) *site.SiteResult {

}
```

- use right click(or command + N)

![](images/demo-1.png)


- then. gen code

```
func transform(d *data.SiteResult) *site.SiteResult {
	return &site.SiteResult{
		SiteAddr:         d.SiteAddr,
		TaskId:           d.TaskId,
		Url:              d.Url,
		Status:           d.Status,
		ResourceType:     d.ResourceType,
		Method:           d.Method,
		RequestBody:      d.RequestBody,
		RequestHeader:    d.RequestHeader,
		ResponseBody:     d.ResponseBody,
		ResponseHeader:   d.ResponseHeader,
		TenantId:         d.TenantId,
		IsTargetDocument: d.IsTargetDocument,
		FailedReason:     d.FailedReason,
		ParentUrl:        d.ParentUrl,
		ParentScreenshot: d.ParentScreenshot,
		DataSource:       d.DataSource,
		Tags:             d.Tags,
	}
}
```