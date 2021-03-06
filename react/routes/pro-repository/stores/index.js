import React, { createContext, useContext, useEffect, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import { Choerodon } from '@choerodon/boot';
import DetailDataSet from '../../repository/stores/DetailDataSet';
import useStore from './useStore';

const Store = createContext();

export function useRepositoryStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const {
      AppState: { currentMenuType: { id } },
      intl: { formatMessage },
      children,
    } = props;
    const intlPrefix = 'c7ncd.repository';
    const url = useMemo(() => `/devops/v1/projects/${id}/project_config`, [id]);

    const detailDs = useMemo(() => new DataSet(DetailDataSet(intlPrefix, formatMessage, url)), [intlPrefix, formatMessage, url]);

    const repositoryStore = useStore();

    const value = {
      ...props,
      prefixCls: 'c7ncd-repository',
      permissions: ['choerodon.code.project.setting.setting-repository.ps.default'],
      intlPrefix,
      promptMsg: formatMessage({ id: `${intlPrefix}.prompt.inform.title` }) + Choerodon.STRING_DEVIDER + formatMessage({ id: `${intlPrefix}.prompt.inform.message` }),
      detailDs,
      repositoryStore,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
