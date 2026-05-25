{{- define "beer-catalogue.name" -}}
{{- .Chart.Name -}}
{{- end -}}

{{- define "beer-catalogue.fullname" -}}
{{- printf "%s-%s" .Release.Name .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "beer-catalogue.labels" -}}
app.kubernetes.io/name: {{ include "beer-catalogue.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ printf "%s-%s" .Chart.Name .Chart.Version }}
{{- end -}}

{{- define "beer-catalogue.selectorLabels" -}}
app.kubernetes.io/name: {{ include "beer-catalogue.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}

{{- define "beer-catalogue.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
{{- default (include "beer-catalogue.fullname" .) .Values.serviceAccount.name -}}
{{- else -}}
{{- default "default" .Values.serviceAccount.name -}}
{{- end -}}
{{- end -}}
