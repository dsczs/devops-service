import React, { useCallback, Fragment } from 'react';
import { Page, Content, Header, Permission, Action, Breadcrumb } from '@choerodon/master';
import { Table, Modal } from 'choerodon-ui/pro';
import { Button } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { withRouter, Link } from 'react-router-dom';
import { observer } from 'mobx-react-lite';
import pick from 'lodash/pick';
import TimePopover from '../../../components/timePopover';
import { useAppServiceStore } from './stores';
import CreateForm from '../modals/creat-form';
import ImportForm from './modal/import-form';
import StatusTag from '../components/status-tag';

import './index.less';
import '../../main.less';

const { Column } = Table;
const modalKey1 = Modal.key();
const modalKey2 = Modal.key();
const modalKey3 = Modal.key();
const modalStyle1 = {
  width: 380,
};
const modalStyle2 = {
  width: '70%',
};

const AppService = withRouter(observer((props) => {
  const {
    intl: { formatMessage },
    AppState: { currentMenuType: { id } },
    intlPrefix,
    prefixCls,
    listDs,
    importDs,
    importTableDs,
    AppStore,
    versionOptions,
    selectedDs,
  } = useAppServiceStore();

  function refresh() {
    listDs.query();
  }

  function renderName({ value, record }) {
    const {
      location: {
        search,
        pathname,
      },
    } = props;
    const canLink = !record.get('fail') && record.get('synchro');
    return (canLink ? (
      <Link
        to={{
          pathname: `${pathname}/detail/${record.get('id')}`,
          search,
        }}
      >
        <span className={`${prefixCls}-table-name`}>{value}</span>
      </Link>) : <span>{value}</span>
    );
  }

  function renderType({ value }) {
    return value && <FormattedMessage id={`${intlPrefix}.type.${value}`} />;
  }

  function renderDate({ value }) {
    return <TimePopover content={value} />;
  }

  function renderUrl({ value }) {
    return (
      <a href={value} rel="nofollow me noopener noreferrer" target="_blank">
        {value ? `../${value.split('/')[value.split('/').length - 1]}` : ''}
      </a>
    );
  }

  function renderStatus({ value, record }) {
    return (
      <StatusTag
        active={value}
        fail={record.get('fail')}
        synchro={record.get('synchro')}
      />
    );
  }

  function renderActions({ record }) {
    const actionData = {
      edit: {
        service: ['devops-service.app-service.update'],
        text: formatMessage({ id: 'edit' }),
        action: openEdit,
      },
      stop: {
        service: ['devops-service.app-service.updateActive'],
        text: formatMessage({ id: 'stop' }),
        action: () => changeActive(false),
      },
      run: {
        service: ['devops-service.app-service.updateActive'],
        text: formatMessage({ id: 'active' }),
        action: () => changeActive(true),
      },
      delete: {
        service: ['devops-service.app-service.delete'],
        text: formatMessage({ id: 'delete' }),
        action: handleDelete,
      },

    };
    let actionItems;
    if (record.get('fail')) {
      actionItems = pick(actionData, ['delete']);
    } else if (record.get('synchro') && record.get('active')) {
      actionItems = pick(actionData, ['edit', 'stop']);
    } else if (record.get('active')) {
      return;
    } else {
      actionItems = pick(actionData, ['run']);
    }
    return (AppStore.getProjectRole === 'owner' ? <Action data={Object.values(actionItems)} /> : null);
  }

  function handleCancel(dataSet) {
    const { current } = dataSet;
    if (current.status === 'add') {
      dataSet.remove(current);
    } else {
      current.reset();
    }
  }

  function openModal(record) {
    Modal.open({
      key: modalKey1,
      drawer: true,
      style: modalStyle1,
      title: <FormattedMessage id={`${intlPrefix}.${record.status !== 'add' ? 'edit' : 'create'}`} />,
      children: <CreateForm
        dataSet={listDs}
        record={record}
        AppStore={AppStore}
        projectId={id}
        intlPrefix={intlPrefix}
        prefixCls={prefixCls}
      />,
      onCancel: () => handleCancel(listDs),
    });
  }

  function openImport() {
    importDs.create();
    Modal.open({
      key: modalKey2,
      drawer: true,
      style: modalStyle2,
      title: <FormattedMessage id={`${intlPrefix}.import`} />,
      children: <ImportForm
        dataSet={importDs}
        tableDs={importTableDs}
        record={importDs.current}
        AppStore={AppStore}
        projectId={id}
        intlPrefix={intlPrefix}
        prefixCls={prefixCls}
        refresh={refresh}
        versionOptions={versionOptions}
        selectedDs={selectedDs}
      />,
      okText: formatMessage({ id: 'import' }),
      afterClose: () => { selectedDs.removeAll(); },
      onCancel: () => handleCancel(importDs),
    });
  }

  function openEdit() {
    AppStore.setAppServiceId(listDs.current.get('id'));
    openModal(listDs.current);
  }

  function handleDelete() {
    listDs.delete(listDs.current);
  }

  async function changeActive(active) {
    if (!active) {
      Modal.open({
        key: modalKey3,
        title: formatMessage({ id: `${intlPrefix}.stop` }),
        children: <FormattedMessage id={`${intlPrefix}.stop.tips`} />,
        onOk: () => handleChangeActive(active),
      });
    } else {
      handleChangeActive(active);
    }
  }

  async function handleChangeActive(active) {
    try {
      if (await AppStore.changeActive(id, listDs.current.get('id'), active)) {
        refresh();
      } else {
        return false;
      }
    } catch (e) {
      Choerodon.handleResponseError(e);
      return false;
    }
  }

  function handleTableFilter(record) {
    return record.status !== 'add';
  }

  return (
    <Page
      service={[
        'devops-service.app-service.pageByOptions',
        'devops-service.app-service.create',
        'devops-service.app-service.importApp',
        'devops-service.app-service.update',
        'devops-service.app-service.updateActive',
        'devops-service.app-service.delete',
      ]}
    >
      <Header title={<FormattedMessage id="app.head" />}>
        <Permission
          service={['devops-service.app-service.create']}
        >
          <Button
            icon="playlist_add"
            onClick={() => openModal(listDs.create())}
          >
            <FormattedMessage id={`${intlPrefix}.create`} />
          </Button>
        </Permission>
        <Permission
          service={['devops-service.app-service.importApp']}
        >
          <Button
            icon="archive"
            onClick={openImport}
          >
            <FormattedMessage id={`${intlPrefix}.import`} />
          </Button>
        </Permission>
        <Button
          icon="refresh"
          onClick={() => refresh()}
        >
          <FormattedMessage id="refresh" />
        </Button>
      </Header>
      <Breadcrumb />
      <Content>
        <Table
          dataSet={listDs}
          border={false}
          queryBar="bar"
          filter={handleTableFilter}
          className={`${prefixCls}.table`}
          rowClassName="c7ncd-table-row-font-color"
        >
          <Column name="name" renderer={renderName} sortable />
          <Column renderer={renderActions} width="0.7rem" />
          <Column name="code" sortable />
          <Column name="type" renderer={renderType} />
          <Column name="repoUrl" renderer={renderUrl} />
          <Column name="creationDate" renderer={renderDate} />
          <Column name="active" renderer={renderStatus} width="0.7rem" />
        </Table>
      </Content>
    </Page>
  );
}));

export default AppService;