export enum YesNo {
  Y = 'Y',
  N = 'N'
}

export interface SettingGottenView {
  id: number;
  tenantId: string;
  dataType: string;
  type: string;
  name: string;
  code: string;
  value?: string;
  description?: string;
  priorityNo: number;
  activeFlag: YesNo;
}

export interface CreateSettingResource {
  tenantId: string;
  dataType: string;
  type: string;
  name: string;
  code: string;
  value?: string;
  description?: string;
  priorityNo: number;
}

export interface UpdateSettingResource {
  tenantId: string;
  dataType: string;
  type: string;
  name: string;
  code: string;
  value?: string;
  description?: string;
  priorityNo: number;
  activeFlag: YesNo;
}
